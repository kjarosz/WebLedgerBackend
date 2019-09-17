package com.webledger.webledger.controller

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.service.TransactionService
import com.webledger.webledger.service.createTestAllocationCenter
import com.webledger.webledger.service.createTestTransaction
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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
}