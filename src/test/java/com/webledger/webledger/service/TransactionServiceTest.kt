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
    lateinit var allocationCenterService: AllocationCenterService

    @MockK
    lateinit var transactionValidationService: TransactionValidationService

    @InjectMockKs
    lateinit var transactionService: TransactionService

    private val transactionTo = TransactionTo(0, LocalDate.now(), TransactionType.Add,
            null, null,
            BigDecimal.ZERO, null, null )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { allocationCenterRepository.findById(null) } returns Optional.ofNullable(null)
        every { allocationCenterService.updateAllocationCenters(any()) } just Runs
    }

    @Test
    fun `saveTransaction - valid transaction is saved`() {
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                null, createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true
        every { transactionValidationService.hasValidCreditAccount(any()) } returns true
        every { transactionRepository.save<Transaction>(any()) } returns newTransaction

        val savedTransaction = transactionService.saveTransaction(transactionTo)

        assertEquals(0, savedTransaction?.id)
        assertEquals(BigDecimal.ZERO, savedTransaction?.amount)
    }

    @Test(expected = InvalidAllocationCenters::class)
    fun `saveTransaction - transaction with invalid allocation centers throws InvalidAllocationCentersException`() {
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns false

        transactionService.saveTransaction(transactionTo)
    }

    @Test(expected = MissingCreditAccount::class)
    fun `saveTransaction - credit transaction without credit account throws MissingCreditAccount`() {
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true
        every { transactionValidationService.hasValidCreditAccount(any()) } returns false

        transactionService.saveTransaction(transactionTo)
    }
}
