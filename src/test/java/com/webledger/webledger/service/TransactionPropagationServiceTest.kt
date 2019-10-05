package com.webledger.webledger.service;

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs;
import io.mockk.junit5.MockKExtension;
import org.junit.Assert
import org.junit.Before
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
internal class TransactionPropagationServiceTest {
    @InjectMockKs
    lateinit var transactionPropagationService: TransactionPropagationService

    lateinit var transactionPropagationServiceSpy: TransactionPropagationService

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        transactionPropagationServiceSpy = spyk(transactionPropagationService)
    }

    @Test
    fun `propagateTransactionChanges - updates allocation centers`() {
        val transaction = createTransactionWithType(TransactionType.Credit)

        every { transactionPropagationServiceSpy.updateAllocationCenters(transaction) } just Runs
        every { transactionPropagationServiceSpy.updateAccounts(transaction) } just Runs
        every { transactionPropagationServiceSpy.reverseUpdateAccount(null) } just Runs

        transactionPropagationServiceSpy.propagateTransactionChanges(transaction, null)

        verify { transactionPropagationServiceSpy.updateAllocationCenters(transaction) }
    }

    @Test
    fun `propagateTransactionChanges - updates accounts`() {
        val transaction = createTransactionWithType(TransactionType.Credit)

        every { transactionPropagationServiceSpy.updateAllocationCenters(transaction) } just Runs
        every { transactionPropagationServiceSpy.updateAccounts(transaction) } just Runs
        every { transactionPropagationServiceSpy.reverseUpdateAccount(null) } just Runs

        transactionPropagationServiceSpy.propagateTransactionChanges(transaction, null)

        verify { transactionPropagationServiceSpy.updateAccounts(transaction) }
    }

    @Test
    fun `propagateTransactionChanges - reverses account updates`() {
        val transaction = createTransactionWithType(TransactionType.Credit)
        val oldTransaction = createTransactionWithType(TransactionType.Credit)

        every { transactionPropagationServiceSpy.updateAllocationCenters(transaction) } just Runs
        every { transactionPropagationServiceSpy.updateAccounts(transaction) } just Runs
        every { transactionPropagationServiceSpy.reverseUpdateAccount(oldTransaction) } just Runs

        transactionPropagationServiceSpy.propagateTransactionChanges(transaction, null)

        verify { transactionPropagationServiceSpy.reverseUpdateAccount(oldTransaction) }
    }

    @Test
    fun `updateAllocationCenters - transaction that adds to destination updates destination amount`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                null, createTestAllocationCenter(0),
                amount, null, null )
        transaction.destinationAllocationCenter!!.amount = BigDecimal.ZERO

        every { transactionPropagationServiceSpy.addsToDestination(transaction) } returns true
        every { transactionPropagationServiceSpy.takesFromSource(transaction) } returns false

        transactionPropagationServiceSpy.updateAllocationCenters(transaction)

        Assert.assertEquals(amount, transaction.destinationAllocationCenter!!.amount)
    }

    @Test
    fun `updateAllocationCenters - transaction that takes from source updates source amount`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                createTestAllocationCenter(0), null,
                amount, null, null )
        transaction.sourceAllocationCenter!!.amount = BigDecimal.TEN

        every { transactionPropagationServiceSpy.addsToDestination(transaction) } returns false
        every { transactionPropagationServiceSpy.takesFromSource(transaction) } returns true

        transactionPropagationServiceSpy.updateAllocationCenters(transaction)

        Assert.assertEquals(BigDecimal.ZERO, transaction.sourceAllocationCenter!!.amount)
    }

    @Test
    fun `updateAccounts - transaction that adds to destination updates destination account`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                null, createTestAllocationCenter(0),
                amount, null, null )
        transaction.destinationAllocationCenter!!.account.amount = BigDecimal.ZERO

        every { transactionPropagationServiceSpy.addsToDestination(transaction) } returns true
        every { transactionPropagationServiceSpy.takesFromSource(transaction) } returns false

        transactionPropagationServiceSpy.updateAccounts(transaction)

        Assert.assertEquals(amount, transaction.destinationAllocationCenter!!.account.amount)
    }

    @Test
    fun `updateAccounts - transaction that takes from source updates source account`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                createTestAllocationCenter(0), null,
                amount, null, null )
        transaction.sourceAllocationCenter!!.account.amount = BigDecimal.TEN

        every { transactionPropagationServiceSpy.addsToDestination(transaction) } returns false
        every { transactionPropagationServiceSpy.takesFromSource(transaction) } returns true

        transactionPropagationServiceSpy.updateAccounts(transaction)

        Assert.assertEquals(BigDecimal.ZERO, transaction.sourceAllocationCenter!!.account.amount)
    }

    @Test
    fun `reverseUpdateAccount - null transaction just passes through`() {
        transactionPropagationService.reverseUpdateAccount(null)
    }

    @Test
    fun `reverseUpdateAccount - transaction that adds to destination removes amount from source`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                null, createTestAllocationCenter(0),
                BigDecimal.TEN, null, null )
        transaction.destinationAllocationCenter!!.account.amount = BigDecimal.TEN

        every { transactionPropagationServiceSpy.addsToDestination(transaction) } returns true
        every { transactionPropagationServiceSpy.takesFromSource(transaction) } returns false

        transactionPropagationServiceSpy.reverseUpdateAccount(transaction)

        Assert.assertEquals(BigDecimal.ZERO, transaction.destinationAllocationCenter!!.account.amount)
    }

    @Test
    fun `reverseUpdateAccount - transaction that takes from source adds amount to source`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Spend,
                createTestAllocationCenter(0), null,
                amount, null, null )
        transaction.sourceAllocationCenter!!.account.amount = BigDecimal.ZERO

        every { transactionPropagationServiceSpy.addsToDestination(transaction) } returns false
        every { transactionPropagationServiceSpy.takesFromSource(transaction) } returns true

        transactionPropagationServiceSpy.reverseUpdateAccount(transaction)

        Assert.assertEquals(amount, transaction.sourceAllocationCenter!!.account.amount)
    }

    @Test
    fun `addsToDestination - add transaction returns true`() {
        val transaction = createTransactionWithType(TransactionType.Add)
        val result: Boolean = transactionPropagationService.addsToDestination(transaction)
        Assert.assertTrue(result)
    }

    @Test
    fun `addsToDestination - transfer transaction returns true`() {
        val transaction = createTransactionWithType(TransactionType.Transfer)
        val result: Boolean = transactionPropagationService.addsToDestination(transaction)
        Assert.assertTrue(result)
    }

    @Test
    fun `addsToDestination - credit transaction returns true`() {
        val transaction = createTransactionWithType(TransactionType.Transfer)
        val result: Boolean = transactionPropagationService.addsToDestination(transaction)
        Assert.assertTrue(result)
    }

    @Test
    fun `addsToDestination - spend transaction returns false`() {
        val transaction = createTransactionWithType(TransactionType.Spend)
        val result: Boolean = transactionPropagationService.addsToDestination(transaction)
        Assert.assertFalse(result)
    }

    @Test
    fun `takesFromSource - transfer returns true`() {
        val transaction = createTransactionWithType(TransactionType.Transfer)
        val result: Boolean = transactionPropagationService.takesFromSource(transaction)
        Assert.assertTrue(result)
    }

    @Test
    fun `takesFromSource - spend returns true`() {
        val transaction = createTransactionWithType(TransactionType.Spend)
        val result: Boolean = transactionPropagationService.takesFromSource(transaction)
        Assert.assertTrue(result)
    }

    @Test
    fun `takesFromSource - credit returns true`() {
        val transaction = createTransactionWithType(TransactionType.Credit)
        val result: Boolean = transactionPropagationService.takesFromSource(transaction)
        Assert.assertTrue(result)
    }

    @Test
    fun `takesFromSource - pay returns true`() {
        val transaction = createTransactionWithType(TransactionType.Pay)
        val result: Boolean = transactionPropagationService.takesFromSource(transaction)
        Assert.assertTrue(result)
    }

    @Test
    fun `takesFromSource - add returns false`() {
        val transaction = createTransactionWithType(TransactionType.Add)
        val result: Boolean = transactionPropagationService.takesFromSource(transaction)
        Assert.assertFalse(result)
    }

    private fun createTransactionWithType(type: TransactionType): Transaction {
        return Transaction(0, LocalDate.now(), type,
                null, null,
                BigDecimal.ZERO, null, null )
    }
}