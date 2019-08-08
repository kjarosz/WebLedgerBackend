package com.webledger.webledger.service

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.repository.AllocationCenterRepository
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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

    @Before
    fun setup() = MockKAnnotations.init(this)

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
        assertTrue(transactionValidationService.hasValidAllocationCenters(transaction))
    }

    private fun createTestTransaction(type: TransactionType,
          sourceCenter: AllocationCenter?, destinationCenter: AllocationCenter?): Transaction {
        return Transaction(0, LocalDate.now(), type, sourceCenter, destinationCenter, BigDecimal.ZERO, LocalDate.now(), null)
    }
}
