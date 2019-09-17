package com.webledger.webledger.controller

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.service.AllocationCenterService
import com.webledger.webledger.transferobject.AllocationCenterTo
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = [ "http://localhost:4200" ])
class AllocationCenterController(
        @Autowired
        val allocationCenterService: AllocationCenterService
) {
    @ApiOperation(value = "Get a list of all allocation centers", response = AllocationCenter::class)
    @GetMapping("/allocationcenters")
    fun getAllAllocationCenters(): ResponseEntity<Iterable<AllocationCenter>?> {
        return ResponseEntity.ok(allocationCenterService.getAllAllocationCenters())
    }

    @ApiOperation(value = "Get an allocation center by id", response = AllocationCenter::class)
    @GetMapping("/allocationcenters/{id}")
    fun getAllocationCenter(@PathVariable("id") id: Int): ResponseEntity<AllocationCenter?> {
        val allocationCenter = allocationCenterService.getAllocationCenter(id)
        return if (allocationCenter != null) {
            ResponseEntity.ok(allocationCenter)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @ApiOperation(value = "Save allocation center", response = AllocationCenter::class)
    @PostMapping("/allocationcenters/save")
    fun saveAllocationCenter(@RequestBody allocationCenterTo: AllocationCenterTo): ResponseEntity<AllocationCenter?> {
        return ResponseEntity.ok(allocationCenterService.saveAllocationCenter(allocationCenterTo))
    }
}
