package com.webledger.webledger.service

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.repository.TransactionRepository
import com.webledger.webledger.transferobject.TransactionTo
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.jupiter.api.Test
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
    fun `saveTransaction - null id creates a new transaction`() {
        val transactionTo = TransactionTo(null, LocalDate.now(), TransactionType.Add, null,
            0, BigDecimal.ZERO, LocalDate.now(), null )
        val transactionSlot = slot<Transaction>()
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                null,
                createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { transactionRepository.save(capture(transactionSlot)) } returns newTransaction
        every { allocationCenterRepository.findById(null) } returns Optional.ofNullable(null)
        every { allocationCenterRepository.findById(transactionTo.destinationAllocationCenterId) } returns Optional.ofNullable(newTransaction.destinationAllocationCenter)
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true

        val savedTransaction = transactionService.saveTransaction(transactionTo)

        val transaction = transactionSlot.captured

        assertEquals(null, transaction.id)
        assertEquals(0, savedTransaction?.id)
        assertEquals(BigDecimal.ZERO, savedTransaction?.amount)
    }

    @Test
    fun `saveTransaction - transaction with id new id is saved as a new transaction`() {
        val transactionTo = TransactionTo(0, LocalDate.now(), TransactionType.Add, null,
                0, BigDecimal.ZERO, LocalDate.now(), null )
        val transactionSlot = slot<Transaction>()
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                null,
                createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { transactionRepository.save(capture(transactionSlot)) } returns newTransaction
        every { allocationCenterRepository.findById(null) } returns Optional.ofNullable(null)
        every { allocationCenterRepository.findById(transactionTo.destinationAllocationCenterId) } returns Optional.ofNullable(newTransaction.destinationAllocationCenter)
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true

        val savedTransaction = transactionService.saveTransaction(transactionTo)

        val transaction = transactionSlot.captured

        assertEquals(0, transaction.id)
        assertEquals(0, savedTransaction?.id)
        assertEquals(BigDecimal.ZERO, savedTransaction?.amount)
    }

    @Test
    fun `saveTransaction - transaction with valid allocation centers is saved`() {
        val transactionTo = TransactionTo(0, LocalDate.now(), TransactionType.Add, null,
                0, BigDecimal.ZERO, LocalDate.now(), null )
        val transactionSlot = slot<Transaction>()
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                null,
                createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { transactionRepository.save(capture(transactionSlot)) } returns newTransaction
        every { allocationCenterRepository.findById(null) } returns Optional.ofNullable(null)
        every { allocationCenterRepository.findById(transactionTo.destinationAllocationCenterId) } returns Optional.ofNullable(newTransaction.destinationAllocationCenter)
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true

        val savedTransaction = transactionService.saveTransaction(transactionTo)

        val transaction = transactionSlot.captured

        assertEquals(0, transaction.id)
        assertEquals(0, savedTransaction?.id)
        assertEquals(BigDecimal.ZERO, savedTransaction?.amount)
    }
}
