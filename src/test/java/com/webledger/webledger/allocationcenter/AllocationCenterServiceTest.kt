package com.webledger.webledger.allocationcenter

import com.webledger.webledger.account.createTestAccount
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
internal class AllocationCenterServiceTest {
    @MockK
    lateinit var allocationCenterRepository: AllocationCenterRepository

    @InjectMocks
    lateinit var allocationCenterService: AllocationCenterService

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `gets all allocation centers`() {
        val allocationCenters = Iterable {
            List(2) { createTestAllocationCenter(it) }.iterator()
        }

        every { allocationCenterRepository.findAll() } returns allocationCenters

        val result = allocationCenterService.getAllAllocationCenters()
        
        assertIterableEquals(allocationCenters, result)
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
