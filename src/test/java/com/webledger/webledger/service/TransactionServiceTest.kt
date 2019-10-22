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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.lang.IllegalArgumentException
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

    val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
            null, createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
            transactionTo.amount, transactionTo.dateBankProcessed, createTestAccount(transactionTo.creditAccountId) )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        transactionServiceSpy = spyk(transactionService)
        every { allocationCenterRepository.findById(null) } throws IllegalArgumentException()
    }

    @Test
    fun `getAllTransactions - gets all transactions`() {
        val transactions = Iterable {
            List(2) { createTestTransaction(it) }.iterator()
        }

        every { transactionRepository.findAll() } returns transactions

        val result = transactionService.getAllTransactions()

        Assertions.assertIterableEquals(transactions, result)
    }

    @Test
    fun `saveTransaction - valid transaction is saved`() {
        every { transactionServiceSpy.createTransactionFromTo(transactionTo) } returns newTransaction
        every { transactionValidationService.validateTransaction(newTransaction) } just Runs
        every { transactionServiceSpy.getExistingTransaction(transactionTo.id!!) } returns null
        every { transactionPropagationService.propagateTransactionChanges(newTransaction, null) } just Runs
        every { transactionRepository.save<Transaction>(newTransaction) } returns newTransaction

        val savedTransaction = transactionServiceSpy.saveTransaction(transactionTo)

        verify { transactionRepository.save(newTransaction) }

        assertEquals(savedTransaction, newTransaction)
    }

    @Test
    fun `saveTransaction - changes are propagated`() {
        val oldTransaction = createTestTransaction(transactionTo.id!!)
        every { transactionServiceSpy.createTransactionFromTo(transactionTo) } returns newTransaction
        every { transactionValidationService.validateTransaction(newTransaction) } just Runs
        every { transactionServiceSpy.getExistingTransaction(transactionTo.id!!) } returns oldTransaction
        every { transactionPropagationService.propagateTransactionChanges(newTransaction, oldTransaction) } just Runs
        every { transactionRepository.save<Transaction>(newTransaction) } returns newTransaction

        transactionServiceSpy.saveTransaction(transactionTo)

        verify { transactionPropagationService.propagateTransactionChanges(newTransaction, oldTransaction) }
    }

    @Test(expected = Exception::class)
    fun `saveTransaction - invalid transaction throws exception and does not save`() {
        every { transactionValidationService.validateTransaction(any()) } throws Exception()

        transactionServiceSpy.saveTransaction(transactionTo)

        verify(exactly = 0) { transactionRepository.save<Transaction>(any()) }
    }

    @Test
    fun `createTransactionFromTo - creates transaction out of transfer object`() {
        val transaction = transactionService.createTransactionFromTo(transactionTo)
        assertEquals(transaction.id, transactionTo.id)
        assertEquals(transaction.transactionType, transactionTo.transactionType)
        assertEquals(transaction.dateCreated, transactionTo.dateCreated)
        assertEquals(transaction.amount, transactionTo.amount)
        assertEquals(transaction.dateBankProcessed, transactionTo.dateBankProcessed)

//        var creditAccount: Account?
    }

    @Test
    fun `createTransactionFromTo - assigns source allocation center from id`() {
        val allocationCenterId = 0
        val allocationCenter = createTestAllocationCenter(allocationCenterId)

        transactionTo.sourceAllocationCenterId = allocationCenterId

        every { allocationCenterRepository.findById(allocationCenterId) } returns Optional.of(allocationCenter)

        val transaction = transactionService.createTransactionFromTo(transactionTo)

        assertEquals(allocationCenter, transaction.sourceAllocationCenter)
    }

    @Test
    fun `createTransactionFromTo - assigns destination allocation center from id`() {
        val allocationCenterId = 0
        val allocationCenter = createTestAllocationCenter(allocationCenterId)

        transactionTo.destinationAllocationCenterId = allocationCenterId

        every { allocationCenterRepository.findById(allocationCenterId) } returns Optional.of(allocationCenter)

        val transaction = transactionService.createTransactionFromTo(transactionTo)

        assertEquals(allocationCenter, transaction.destinationAllocationCenter)
    }

    @Test
    fun `getExistingTransaction - existing transaction is returned`() {
        val transactionId: Int? = 1
        val transaction = createTestTransaction(transactionId!!)

        every { transactionRepository.findByIdOrNull(transactionId!!) } returns transaction

        val retrievedTransaction = transactionService.getExistingTransaction(transactionId)

        assertEquals(transaction, retrievedTransaction)
    }

    @Test
    fun `getExistingTransaction - transaction not found returns null`() {
        val transactionId: Int? = 1

        every { transactionRepository.findByIdOrNull(transactionId!!) } returns null

        val retrievedTransaction = transactionService.getExistingTransaction(transactionId)

        assertNull(retrievedTransaction)
    }

    @Test
    fun `getExistingTransaction - null transaction id returns null`() {
        val retrievedTransaction = transactionService.getExistingTransaction(null)

        assertNull(retrievedTransaction)
    }

}

fun createTestTransaction(id: Int): Transaction {
    return Transaction(
        id,
        LocalDate.now(),
        TransactionType.Add,
        null,
        null,
        BigDecimal.ZERO,
        LocalDate.now(),
        null
    )
}
