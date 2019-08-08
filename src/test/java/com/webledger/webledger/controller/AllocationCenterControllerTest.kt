package com.webledger.webledger.controller

import com.webledger.webledger.service.AllocationCenterService
import com.webledger.webledger.service.createTestAllocationCenter
import com.webledger.webledger.transferobject.AllocationCenterTo
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

@ExtendWith(MockKExtension::class)
internal class AllocationCenterControllerTest {
    @MockK
    lateinit var allocationCenterService: AllocationCenterService

    @InjectMockKs
    lateinit var allocationCenterController: AllocationCenterController

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `getAllAllocationCenters - retrieve all allocation centers`() {
        val allocationCenters = Iterable {
            List(2) { createTestAllocationCenter(1) }.iterator()
        }

        every { allocationCenterService.getAllAllocationCenters() } returns allocationCenters

        val responseEntity = allocationCenterController.getAllAllocationCenters()

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertIterableEquals(allocationCenters, responseEntity.body)
    }

    @Test
    fun `getAllocationCenter - return particular allocation center`() {
        val allocationCenterId = 1
        val allocationCenter = createTestAllocationCenter(allocationCenterId)

        every { allocationCenterService.getAllocationCenter(allocationCenterId) } returns allocationCenter

        val responseEntity = allocationCenterController.getAllocationCenter(allocationCenterId)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(allocationCenter, responseEntity.body)
    }

    @Test
    fun `getAllocationCenter - return a 404 response when no allocation center found`() {
        val allocationCenterId = 1

        every { allocationCenterService.getAllocationCenter(allocationCenterId) } returns null

        val responseEntity = allocationCenterController.getAllocationCenter(allocationCenterId)

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
        assertNull(responseEntity.body)

    }

    @Test
    fun `saveAllocationCenter - returns code 200 when account saved successfully`() {
        val allocationCenterTo = AllocationCenterTo(null, "New AC", BigDecimal.ONE, 1, 1)
        val savedAllocationCenter = createTestAllocationCenter(1)

        every { allocationCenterService.saveAllocationCenter(allocationCenterTo) } returns savedAllocationCenter

        val responseEntity = allocationCenterController.saveAllocationCenter(allocationCenterTo)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(savedAllocationCenter, responseEntity.body)
    }
}
