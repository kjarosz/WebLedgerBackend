package com.webledger.webledger.service

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.controller.GlobalExceptionHandler.AccountNotFoundException
import com.webledger.webledger.exceptions.AllocationCenterNotFoundException
import com.webledger.webledger.exceptions.DeleteEntityWithChildrenException
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.transferobject.AllocationCenterTo
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.util.*

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
        every { allocationCenterRepository.findById(0) } returns Optional.empty()
        every { allocationCenterRepository.save(capture(allocationCenterSlot)) } returns newAllocationCenter

        val savedAllocationCenter = allocationCenterService.saveAllocationCenter(allocationCenterTo)

        val allocationCenter = allocationCenterSlot.captured

        assertEquals(0, allocationCenter.id)
        assertEquals(0, savedAllocationCenter?.id)
        assertEquals(BigDecimal.ZERO, savedAllocationCenter?.amount)
    }

    @Test(expected = AllocationCenterNotFoundException::class)
    fun `deleteAllocationCenter - non existent allocation center throws exception`() {
        val id = 1

        every { allocationCenterRepository.findByIdOrNull(id) } returns null

        allocationCenterService.deleteAllocationCenter(id)
    }

    @Test
    fun `deleteAllocationCenter - valid allocation center is deleted`() {
        val id = 1
        val allocationCenter = createTestAllocationCenter(id)

        every { allocationCenterRepository.findByIdOrNull(id) } returns allocationCenter
        every { allocationCenterRepository.delete(allocationCenter) } just Runs

        allocationCenterService.deleteAllocationCenter(id)

        verify(exactly = 1) { allocationCenterRepository.delete(allocationCenter) }
    }

    @Test(expected = DeleteEntityWithChildrenException::class)
    fun `deleteAllocationCenter - allocation center with sources transactions throws exception`() {
        val id = 1
        val allocationCenter = createTestAllocationCenter(id)
        allocationCenter.sourcesTransactions = listOf(createTestTransaction(id))

        every { allocationCenterRepository.findByIdOrNull(id) } returns allocationCenter

        allocationCenterService.deleteAllocationCenter(id)
    }

    @Test(expected = DeleteEntityWithChildrenException::class)
    fun `deleteAllocationCenter - allocation center with destination transactions throws exception`() {
        val id = 1
        val allocationCenter = createTestAllocationCenter(id)
        allocationCenter.destinationTransactions = listOf(createTestTransaction(id))

        every { allocationCenterRepository.findByIdOrNull(id) } returns allocationCenter

        allocationCenterService.deleteAllocationCenter(id)
    }
}

fun createTestAllocationCenter(id: Int?): AllocationCenter {
    return AllocationCenter(
            id,
            "Allocation Center $id",
            BigDecimal.ZERO,
            BigDecimal.ONE,
            createTestAccount(id),
            null,
            null,
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
