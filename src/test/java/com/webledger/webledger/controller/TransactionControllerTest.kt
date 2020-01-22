package com.webledger.webledger.controller

import com.webledger.webledger.entity.AccountType
import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.service.TransactionService
import com.webledger.webledger.service.createTestAccount
import com.webledger.webledger.service.createTestTransaction
import com.webledger.webledger.transferobject.AccountTo
import com.webledger.webledger.transferobject.TransactionTo
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
internal class TransactionControllerTest {

    @MockK
    lateinit var transactionService: TransactionService

    @InjectMockKs
    lateinit var transactionController: TransactionController

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `getAllTransactions - retrieve all transactions`() {
        val transactions = Iterable {
            List(2) { createTestTransaction(it) }.iterator()
        }

        every { transactionService.getAllTransactions() } returns transactions

        val response = transactionController.getAllTransactions()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(transactions, response.body)
    }

    @Test
    fun `getTransaction - Returns 200 success and transaction when transaction is found`() {
        val transactionId = 1
        val transaction = createTestTransaction(transactionId)

        every { transactionService.getTransaction(transactionId) } returns transaction

        val response = transactionController.getTransaction(transactionId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(transaction, response.body)
    }

    @Test
    fun `getTransaction - Returns 404 when transaction is null`() {
        val transactionId = 1

        every { transactionService.getTransaction(transactionId) } returns null

        val response = transactionController.getTransaction(transactionId)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `saveTransaction - Returns 200 success when transaction is saved`() {
        val transactionId = 1
        val transactionTo = TransactionTo(null, LocalDate.now(), TransactionType.Credit,
                null, null, BigDecimal.ZERO, null, null)
        val transaction = createTestTransaction(transactionId)

        every { transactionService.saveTransaction(transactionTo) } returns transaction

        val responseEntity = transactionController.saveTransaction(transactionTo)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(transaction, responseEntity.body)
    }

    @Test
    fun `getTransactionTypes - return list of transaction types`() {
        val responseEntity = transactionController.getTransactionTypes()

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertArrayEquals(TransactionType.values(), responseEntity.body)
    }

    @Test
    fun `deleteTransaction - return no content on successful delete`() {
        val transactionId = 1

        every { transactionService.deleteTransaction(transactionId) } just Runs

        val responseEntity = transactionController.deleteTransaction(transactionId)

        assertEquals(responseEntity.statusCode, HttpStatus.NO_CONTENT)
    }
}