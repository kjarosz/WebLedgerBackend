package com.webledger.webledger.service

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.exceptions.InvalidAllocationCenters
import com.webledger.webledger.exceptions.MissingCreditAccount
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
internal class TransactionValidationServiceTest {
    @InjectMocks
    var transactionValidationService: TransactionValidationService = TransactionValidationService()

    lateinit var transactionValidationServiceSpy: TransactionValidationService

    val transaction = createTestTransaction(TransactionType.Credit, null, null)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        transactionValidationServiceSpy = spyk(transactionValidationService)
    }

    @Test(expected = InvalidAllocationCenters::class)
    fun `validateTransaction - transaction with invalid allocation centers throws InvalidAllocationCentersException`() {
        every { transactionValidationServiceSpy.hasValidAllocationCenters(any()) } returns false

        transactionValidationServiceSpy.validateTransaction(transaction)
    }

    @Test(expected = MissingCreditAccount::class)
    fun `validateTransaction - credit transaction without credit account throws MissingCreditAccount`() {
        every { transactionValidationServiceSpy.hasValidAllocationCenters(any()) } returns true
        every { transactionValidationServiceSpy.hasValidCreditAccount(any()) } returns false

        transactionValidationServiceSpy.validateTransaction(transaction)
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Add and existing destination center is valid`() {
        val transaction = createTestTransaction(TransactionType.Add, null, createTestAllocationCenter(0))
        assertTrue(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Add and null destination center is invalid`() {
        val transaction = createTestTransaction(TransactionType.Add, null, null)
        assertFalse(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Spend and existing source center is valid`() {
        val transaction = createTestTransaction(TransactionType.Spend, createTestAllocationCenter(0), null)
        assertTrue(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Spend and null source center is invalid`() {
        val transaction = createTestTransaction(TransactionType.Spend, null, null)
        assertFalse(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Transfer and existing source and destination center is valid`() {
        val transaction = createTestTransaction(TransactionType.Transfer, createTestAllocationCenter(0), createTestAllocationCenter(0))
        assertTrue(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Transfer, null source, and existing destination center is invalid`() {
        val transaction = createTestTransaction(TransactionType.Transfer, null, createTestAllocationCenter(0))
        assertFalse(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Transfer, null destination, and existing source center is invalid`() {
        val transaction = createTestTransaction(TransactionType.Transfer, createTestAllocationCenter(0), null)
        assertFalse(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Transfer, null source and destination center is invalid`() {
        val transaction = createTestTransaction(TransactionType.Transfer, null, null)
        assertFalse(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Credit and existing source is valid`() {
        val transaction = createTestTransaction(TransactionType.Credit, createTestAllocationCenter(0), null)
        assertTrue(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    @Test
    fun `hasValidAllocationCenters - transaction with type Credit and null source is invalid`() {
        val transaction = createTestTransaction(TransactionType.Credit, null, null)
        assertFalse(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    private fun createTestTransaction(type: TransactionType,
          sourceCenter: AllocationCenter?, destinationCenter: AllocationCenter?): Transaction {
        return Transaction(0, LocalDate.now(), type, sourceCenter, destinationCenter, BigDecimal.ZERO, LocalDate.now(), null)
    }

    @Test
    fun `hasValidCreditAccount - transaction with type Credit and null creditAccount is invalid`() {
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Credit,
                createTestAllocationCenter(0), null, BigDecimal.ZERO, LocalDate.now(),
                null)
        assertFalse(transactionValidationService.hasValidCreditAccount(transaction))
    }

    @Test
    fun `hasValidCreditAccount - transaction with type Credit and existing creditAccount is valid`() {
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Credit,
                createTestAllocationCenter(0), null, BigDecimal.ZERO, LocalDate.now(),
                createTestAccount(0))
        assertTrue(transactionValidationService.hasValidCreditAccount(transaction))
    }
}
