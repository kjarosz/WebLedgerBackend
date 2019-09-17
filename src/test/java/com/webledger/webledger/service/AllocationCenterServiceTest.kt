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
