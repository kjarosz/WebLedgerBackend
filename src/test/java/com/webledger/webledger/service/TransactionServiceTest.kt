package com.webledger.webledger.service

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.exceptions.InvalidAllocationCenters
import com.webledger.webledger.exceptions.MissingCreditAccount
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.repository.TransactionRepository
import com.webledger.webledger.transferobject.TransactionTo
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
internal class TransactionServiceTest {
    @MockK
    lateinit var transactionRepository: TransactionRepository

    @MockK
    lateinit var allocationCenterRepository: AllocationCenterRepository

    @MockK
    lateinit var transactionValidationService: TransactionValidationService

    @InjectMockKs
    lateinit var transactionService: TransactionService


    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `saveTransaction - valid transaction is saved`() {
        val transactionTo = TransactionTo(null, LocalDate.now(), TransactionType.Add, null,
                null, BigDecimal.ZERO, null, null )
        val transactionSlot = slot<Transaction>()
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                null, createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { allocationCenterRepository.findById(null) } returns Optional.ofNullable(null)
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true
        every { transactionValidationService.hasValidCreditAccount(any()) } returns true
        every { transactionRepository.save(capture(transactionSlot)) } returns newTransaction

        val savedTransaction = transactionService.saveTransaction(transactionTo)

        val transaction = transactionSlot.captured

        assertEquals(null, transaction.id)
        assertEquals(0, savedTransaction?.id)
        assertEquals(BigDecimal.ZERO, savedTransaction?.amount)
    }

    @Test(expected = InvalidAllocationCenters::class)
    fun `saveTransaction - transaction with invalid allocation centers throws InvalidAllocationCentersException`() {
        val transactionTo = TransactionTo(0, LocalDate.now(), TransactionType.Add, null,
                null, BigDecimal.ZERO, null, null )

        every { allocationCenterRepository.findById(any()) } returns Optional.ofNullable(null)
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns false

        transactionService.saveTransaction(transactionTo)
    }

    @Test(expected = MissingCreditAccount::class)
    fun `saveTransaction - credit transaction without credit account throws MissingCreditAccount`() {
        val transactionTo = TransactionTo(0, LocalDate.now(), TransactionType.Add, null,
                null, BigDecimal.ZERO, null, null )

        every { allocationCenterRepository.findById(any()) } returns Optional.ofNullable(null)
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true
        every { transactionValidationService.hasValidCreditAccount(any()) } returns false

        transactionService.saveTransaction(transactionTo)
    }
}
