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
import org.junit.Assert.*
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
    lateinit var transactionPropagationService: TransactionPropagationService

    @MockK
    lateinit var transactionValidationService: TransactionValidationService

    @InjectMockKs
    lateinit var transactionService: TransactionService

    lateinit var transactionServiceSpy: TransactionService

    private val transactionTo = TransactionTo(0, LocalDate.now(), TransactionType.Add,
            null, null,
            BigDecimal.ZERO, null, null )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        transactionServiceSpy = spyk(transactionService)
        every { allocationCenterRepository.findById(null) } returns Optional.ofNullable(null)
    }

    @Test
    fun `saveTransaction - valid transaction is saved`() {
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                null, createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { transactionValidationService.validateTransaction(any()) } just Runs
        every { transactionPropagationService.propagateTransactionChanges(any()) } just Runs
        every { transactionRepository.save<Transaction>(any()) } returns newTransaction

        val savedTransaction = transactionServiceSpy.saveTransaction(transactionTo)

        assertEquals(0, savedTransaction?.id)
        assertEquals(BigDecimal.ZERO, savedTransaction?.amount)
    }

    @Test
    fun `saveTransaction - changes are propagated`() {
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                null, createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { transactionValidationService.validateTransaction(any()) } just Runs
        every { transactionPropagationService.propagateTransactionChanges(any()) } just Runs
        every { transactionRepository.save<Transaction>(any()) } returns newTransaction

        val savedTransaction = transactionServiceSpy.saveTransaction(transactionTo)

        verify { transactionPropagationService.propagateTransactionChanges(any()) }
    }

    @Test(expected = Exception::class)
    fun `saveTransaction - transaction with invalid allocation centers throws InvalidAllocationCentersException`() {
        every { transactionValidationService.validateTransaction(any()) } throws Exception()

        transactionServiceSpy.saveTransaction(transactionTo)
    }

}
