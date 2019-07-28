package com.webledger.webledger.service

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.repository.TransactionRepository
import com.webledger.webledger.transferobject.TransactionTo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
internal class TransactionServiceTest {
    @MockK
    lateinit var transactionRepository: TransactionRepository

    @InjectMockKs
    lateinit var transactionService: TransactionService

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `saveTransaction - null id creates a new transaction`() {
        val transactionTo = TransactionTo(null, LocalDate.now(), TransactionType.Add, null,
            null, BigDecimal.ZERO, LocalDate.now(), null )
        val transactionSlot = slot<Transaction>()
        val newTransaction = Transaction(0, transactionTo.dateCreated, transactionTo.transactionType,
                createTestAllocationCenter(transactionTo.sourceAllocationCenterId),
                createTestAllocationCenter(transactionTo.destinationAllocationCenterId),
                transactionTo.amount, transactionTo.dateBankProcessed, transactionTo.creditAccount )

        every { transactionRepository.save(capture(transactionSlot)) } returns newTransaction

        val savedTransaction = transactionService.saveTransaction(transactionTo)

        val transaction = transactionSlot.captured

        assertEquals(null, transaction.id)
        assertEquals(0, savedTransaction?.id)
        assertEquals(BigDecimal.ZERO, savedTransaction?.amount)
    }
}
