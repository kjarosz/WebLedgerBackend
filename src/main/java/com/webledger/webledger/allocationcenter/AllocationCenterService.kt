package com.webledger.webledger.allocationcenter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AllocationCenterService(
        @Autowired
        val allocationCenterRepository: AllocationCenterRepository
) {
    fun getAllAllocationCenters(): Iterable<AllocationCenter>? = allocationCenterRepository.findAll()

    fun getAllocationCenter(id: Int) =  allocationCenterRepository.findByIdOrNull(id)

}