package com.webledger.webledger.allocationcenter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AllocationCenterController(
        @Autowired
        val allocationCenterService: AllocationCenterService
) {
    @GetMapping("/allocationcenters")
    fun getAllAllocationCenters(): ResponseEntity<Iterable<AllocationCenter>?> {
        return ResponseEntity.ok(allocationCenterService.getAllAllocationCenters())
    }
}
