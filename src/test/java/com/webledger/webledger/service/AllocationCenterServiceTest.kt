package com.webledger.webledger.service

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.exceptions.AccountNotFoundException
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.transferobject.AllocationCenterTo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
internal class AllocationCenterServiceTest {
    @MockK
    lateinit var accountService: AccountService

    @MockK
    lateinit var allocationCenterRepository: AllocationCenterRepository

    @InjectMockKs
    lateinit var allocationCenterService: AllocationCenterService

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `getAllAllocationCenters - gets all allocation centers`() {
        val allocationCenters = Iterable {
            List(2) { createTestAllocationCenter(it) }.iterator()
        }

        every { allocationCenterRepository.findAll() } returns allocationCenters

        val result = allocationCenterService.getAllAllocationCenters()

        assertIterableEquals(allocationCenters, result)
    }

    @Test
    fun `getAllocationCenter - gets particular allocation center`() {
        val allocationCenterId = 1
        val allocationCenter = createTestAllocationCenter(allocationCenterId)

        every { allocationCenterRepository.findByIdOrNull(allocationCenterId) } returns allocationCenter

        val result = allocationCenterService.getAllocationCenter(allocationCenterId)

        assertEquals(allocationCenter, result)
    }

    @Test
    fun `saveAllocationCenter - saves allocation center with null id as new allocation center`() {
        val allocationCenterTo = createTestAllocationCenterTo(null, 0)
        val allocationCenterSlot = slot<AllocationCenter>()
        val newAllocationCenter = createTestAllocationCenter(0)
        val account = createTestAccount(0)

        every { accountService.getAccount(0) } returns account
        every { allocationCenterRepository.save(capture(allocationCenterSlot)) } returns newAllocationCenter

        val savedAllocationCenter = allocationCenterService.saveAllocationCenter(allocationCenterTo)

        val allocationCenter = allocationCenterSlot.captured

        assertEquals(null, allocationCenter.id)
        assertEquals(0, savedAllocationCenter?.id)
        assertEquals(BigDecimal.ZERO, savedAllocationCenter?.amount)
    }

    @Test(expected = AccountNotFoundException::class)
    fun `saveAllocationCenter - nonexistent account throws an invalid account error`() {
        val allocationCenterTo = createTestAllocationCenterTo(null, 0)

        every { accountService.getAccount(0) } returns null

        allocationCenterService.saveAllocationCenter(allocationCenterTo)
    }

    @Test
    fun `saveAllocationCenter - allocation center with id that is not used yet is saved as a new allocation center`() {
        val allocationCenterTo = createTestAllocationCenterTo(0, 0)
        val allocationCenterSlot = slot<AllocationCenter>()
        val newAllocationCenter = createTestAllocationCenter(0)
        val account = createTestAccount(0)

        every { accountService.getAccount(0) } returns account
        every { allocationCenterRepository.findById(0) } returns null
        every { allocationCenterRepository.save(capture(allocationCenterSlot)) } returns newAllocationCenter

        val savedAllocationCenter = allocationCenterService.saveAllocationCenter(allocationCenterTo)

        val allocationCenter = allocationCenterSlot.captured

        assertEquals(0, allocationCenter.id)
        assertEquals(0, savedAllocationCenter?.id)
        assertEquals(BigDecimal.ZERO, savedAllocationCenter?.amount)
    }

    @Test
    fun `updateAllocationCenters - add transaction adds amount to destination allocation center`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Add,
                null, createTestAllocationCenter(0),
                amount, null, null )
        transaction.destinationAllocationCenter!!.amount = BigDecimal.ZERO

        allocationCenterService.updateAllocationCenters(transaction)

        Assert.assertEquals(amount, transaction.destinationAllocationCenter!!.amount)
    }

    @Test
    fun `updateAllocationCenters - transfer transaction moves amount from source to destination allocation center`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Transfer,
                createTestAllocationCenter(0), createTestAllocationCenter(0),
                amount, null, null )
        transaction.sourceAllocationCenter!!.amount = amount
        transaction.destinationAllocationCenter!!.amount = BigDecimal.ZERO

        allocationCenterService.updateAllocationCenters(transaction)

        Assert.assertEquals(amount, transaction.destinationAllocationCenter!!.amount)
        Assert.assertEquals(BigDecimal.ZERO, transaction.sourceAllocationCenter!!.amount)
    }

    @Test
    fun `updateAllocationCenters - spend transaction removes amount from source allocation center`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Spend,
                createTestAllocationCenter(0), null,
                amount, null, null )
        transaction.sourceAllocationCenter!!.amount = amount

        allocationCenterService.updateAllocationCenters(transaction)

        Assert.assertEquals(BigDecimal.ZERO, transaction.sourceAllocationCenter!!.amount)
    }

    @Test
    fun `updateAllocationCenters - credit transaction removes amount from source allocation center`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Credit,
                createTestAllocationCenter(0), createTestAllocationCenter(0),
                amount, null, null )
        transaction.sourceAllocationCenter!!.amount = amount
        transaction.destinationAllocationCenter!!.amount = BigDecimal.ZERO

        allocationCenterService.updateAllocationCenters(transaction)

        Assert.assertEquals(BigDecimal.ZERO, transaction.sourceAllocationCenter!!.amount)
        Assert.assertEquals(amount, transaction.destinationAllocationCenter!!.amount)
    }

    @Test
    fun `updateAllocationCenters - pay transaction removes amount from source allocation center`() {
        val amount = BigDecimal.TEN
        val transaction = Transaction(0, LocalDate.now(), TransactionType.Pay,
                createTestAllocationCenter(0), null,
                amount, null, null )
        transaction.sourceAllocationCenter!!.amount = amount

        allocationCenterService.updateAllocationCenters(transaction)

        Assert.assertEquals(BigDecimal.ZERO, transaction.sourceAllocationCenter!!.amount)
    }
}

fun createTestAllocationCenter(id: Int?): AllocationCenter {
    return AllocationCenter(
            id,
            "Allocation Center $id",
            BigDecimal.ZERO,
            BigDecimal.ONE,
            createTestAccount(id)!!,
            null
    )
}

fun createTestAllocationCenterTo(id: Int?, accountId: Int): AllocationCenterTo {
    return AllocationCenterTo(
            id,
            "Allocation Center $id",
            BigDecimal.ONE,
            accountId,
            null
    )
}
