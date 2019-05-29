package com.webledger.webledger.allocationcenter

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

@ExtendWith(MockKExtension::class)
internal class AllocationCenterControllerTest {
    @MockK
    lateinit var allocationCenterService: AllocationCenterService

    @InjectMockKs
    lateinit var allocationCenterController: AllocationCenterController

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `retrieve all allocation centers`() {
        val allocationCenters = Iterable {
            List(2) { createTestAllocationCenter(1) }.iterator()
        }

        every { allocationCenterService.getAllAllocationCenters() } returns allocationCenters

        val responseEntity = allocationCenterController.getAllAllocationCenters()

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertIterableEquals(allocationCenters, responseEntity.body)
    }

    @Test
    fun `return particular allocation center`() {
        val allocationCenterId = 1
        val allocationCenter = createTestAllocationCenter(allocationCenterId)

        every { allocationCenterService.getAllocationCenter(allocationCenterId) } returns allocationCenter

        val responseEntity = allocationCenterController.getAllocationCenter(allocationCenterId)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(allocationCenter, responseEntity.body)
    }

    @Test
    fun `return a 404 response when no allocation center found`() {
        val allocationCenterId = 1

        every { allocationCenterService.getAllocationCenter(allocationCenterId) } returns null

        val responseEntity = allocationCenterController.getAllocationCenter(allocationCenterId)

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
        assertNull(responseEntity.body)

    }
}
