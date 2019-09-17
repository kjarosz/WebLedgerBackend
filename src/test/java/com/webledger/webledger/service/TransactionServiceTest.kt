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
import org.junit.Assert
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
    lateinit var allocationCenterService: AllocationCenterService

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

        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true
        every { transactionValidationService.hasValidCreditAccount(any()) } returns true
        every { transactionServiceSpy.updateAllocationCenters(any()) } just Runs
        every { transactionServiceSpy.updateAccounts(any()) } just Runs
        every { transactionRepository.save<Transaction>(any()) } returns newTransaction

        val savedTransaction = transactionServiceSpy.saveTransaction(transactionTo)

        assertEquals(0, savedTransaction?.id)
        assertEquals(BigDecimal.ZERO, savedTransaction?.amount)
    }

    @Test
    fun `saveTransaction - allocation centers are updated`() {
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                null, createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true
        every { transactionValidationService.hasValidCreditAccount(any()) } returns true
        every { transactionServiceSpy.updateAllocationCenters(any()) } just Runs
        every { transactionServiceSpy.updateAccounts(any()) } just Runs
        every { transactionRepository.save<Transaction>(any()) } returns newTransaction

        val savedTransaction = transactionServiceSpy.saveTransaction(transactionTo)

        verify { transactionServiceSpy.updateAllocationCenters(any()) }
    }

    @Test
    fun `saveTransaction - accounts are updated`() {
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                null, createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true
        every { transactionValidationService.hasValidCreditAccount(any()) } returns true
        every { transactionServiceSpy.updateAllocationCenters(any()) } just Runs
        every { transactionServiceSpy.updateAccounts(any()) } just Runs
        every { transactionRepository.save<Transaction>(any()) } returns newTransaction

        val savedTransaction = transactionServiceSpy.saveTransaction(transactionTo)

        verify { transactionServiceSpy.updateAccounts(any()) }
    }

    @Test(expected = InvalidAllocationCenters::class)
    fun `saveTransaction - transaction with invalid allocation centers throws InvalidAllocationCentersException`() {
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns false

        transactionServiceSpy.saveTransaction(transactionTo)
    }

    @Test(expected = MissingCreditAccount::class)
    fun `saveTransaction - credit transaction without credit account throws MissingCreditAccount`() {
        every { transactionValidationService.hasValidAllocationCenters(any()) } returns true
        every { transactionValidationService.hasValidCreditAccount(any()) } returns false

        transactionServiceSpy.saveTransaction(transactionTo)
    }

    @Test
    fun `updateAllocationCenters - transaction that adds to destination updates destination amount`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                null, createTestAllocationCenter(0),
                amount, null, null )
        transaction.destinationAllocationCenter!!.amount = BigDecimal.ZERO

        every { transactionServiceSpy.addsToDestination(transaction) } returns true
        every { transactionServiceSpy.takesFromSource(transaction) } returns false

        transactionServiceSpy.updateAllocationCenters(transaction)

        Assert.assertEquals(amount, transaction.destinationAllocationCenter!!.amount)
    }

    @Test
    fun `updateAllocationCenters - transaction that takes from source updates source amount`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                createTestAllocationCenter(0), null,
                amount, null, null )
        transaction.sourceAllocationCenter!!.amount = BigDecimal.TEN

        every { transactionServiceSpy.addsToDestination(transaction) } returns false
        every { transactionServiceSpy.takesFromSource(transaction) } returns true

        transactionServiceSpy.updateAllocationCenters(transaction)

        Assert.assertEquals(BigDecimal.ZERO, transaction.sourceAllocationCenter!!.amount)
    }

    @Test
    fun `updateAccounts - transaction that adds to destination updates destination account`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                null, createTestAllocationCenter(0),
                amount, null, null )
        transaction.destinationAllocationCenter!!.account.amount = BigDecimal.ZERO

        every { transactionServiceSpy.addsToDestination(transaction) } returns true
        every { transactionServiceSpy.takesFromSource(transaction) } returns false

        transactionServiceSpy.updateAccounts(transaction)

        Assert.assertEquals(amount, transaction.destinationAllocationCenter!!.account.amount)
    }

    @Test
    fun `updateAccounts - transaction that takes from source updates source account`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                createTestAllocationCenter(0), null,
                amount, null, null )
        transaction.sourceAllocationCenter!!.account.amount = BigDecimal.TEN

        every { transactionServiceSpy.addsToDestination(transaction) } returns false
        every { transactionServiceSpy.takesFromSource(transaction) } returns true

        transactionServiceSpy.updateAccounts(transaction)

        Assert.assertEquals(BigDecimal.ZERO, transaction.sourceAllocationCenter!!.account.amount)
    }


    @Test
    fun `addsToDestination - add transaction returns true`() {
        val transaction = createTransactionWithType(TransactionType.Add)
        val result: Boolean = transactionService.addsToDestination(transaction)
        assertTrue(result)
    }

    @Test
    fun `addsToDestination - transfer transaction returns true`() {
        val transaction = createTransactionWithType(TransactionType.Transfer)
        val result: Boolean = transactionService.addsToDestination(transaction)
        assertTrue(result)
    }

    @Test
    fun `addsToDestination - credit transaction returns true`() {
        val transaction = createTransactionWithType(TransactionType.Transfer)
        val result: Boolean = transactionService.addsToDestination(transaction)
        assertTrue(result)
    }

    @Test
    fun `addsToDestination - spend transaction returns false`() {
        val transaction = createTransactionWithType(TransactionType.Spend)
        val result: Boolean = transactionService.addsToDestination(transaction)
        assertFalse(result)
    }

    @Test
    fun `takesFromSource - transfer returns true`() {
        val transaction = createTransactionWithType(TransactionType.Transfer)
        val result: Boolean = transactionService.takesFromSource(transaction)
        assertTrue(result)
    }

    @Test
    fun `takesFromSource - spend returns true`() {
        val transaction = createTransactionWithType(TransactionType.Spend)
        val result: Boolean = transactionService.takesFromSource(transaction)
        assertTrue(result)
    }

    @Test
    fun `takesFromSource - credit returns true`() {
        val transaction = createTransactionWithType(TransactionType.Credit)
        val result: Boolean = transactionService.takesFromSource(transaction)
        assertTrue(result)
    }

    @Test
    fun `takesFromSource - pay returns true`() {
        val transaction = createTransactionWithType(TransactionType.Pay)
        val result: Boolean = transactionService.takesFromSource(transaction)
        assertTrue(result)
    }

    @Test
    fun `takesFromSource - add returns false`() {
        val transaction = createTransactionWithType(TransactionType.Add)
        val result: Boolean = transactionService.takesFromSource(transaction)
        assertFalse(result)
    }

    private fun createTransactionWithType(type: TransactionType): Transaction {
        return Transaction(0, LocalDate.now(), type,
                null, null,
                BigDecimal.ZERO, null, null )
    }
}
