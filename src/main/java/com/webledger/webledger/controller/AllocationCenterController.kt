package com.webledger.webledger.controller

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.service.AllocationCenterService
import com.webledger.webledger.transferobject.AllocationCenterTo
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@CrossOrigin(origins = [ "http://localhost:4200" ])
@RequestMapping("/allocationcenters")
class AllocationCenterController(
        @Autowired
        val allocationCenterService: AllocationCenterService
) {
    private val logger = LoggerFactory.getLogger(AllocationCenterController::class.simpleName)

    @ApiOperation(value = "Get a list of all allocation centers", response = AllocationCenter::class)
    @GetMapping
    fun getAllAllocationCenters(): ResponseEntity<Iterable<AllocationCenter>> {
        return ResponseEntity.ok(allocationCenterService.getAllAllocationCenters())
    }

    @ApiOperation(value = "Get an allocation center by id", response = AllocationCenter::class)
    @GetMapping("/{id}")
    fun getAllocationCenter(@PathVariable("id") id: Int): ResponseEntity<AllocationCenter?> {
        val allocationCenter = allocationCenterService.getAllocationCenter(id)
        return if (allocationCenter != null) {
            ResponseEntity.ok(allocationCenter)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @ApiOperation(value = "Save allocation center", response = AllocationCenter::class)
    @PostMapping
    fun saveAllocationCenter(@RequestBody allocationCenterTo: AllocationCenterTo): ResponseEntity<AllocationCenter?> {
        logger.info("Saving allocation center: $allocationCenterTo")
        val allocationCenter = allocationCenterService.saveAllocationCenter(allocationCenterTo)
        logger.debug("Saved allocation center: $allocationCenter")
        val allocationCenterUri = UriComponentsBuilder
                .fromUriString("/allocationcenters/{id}")
                .buildAndExpand(mapOf(Pair("id", allocationCenter?.id)))
                .toUri()
        return ResponseEntity.created(allocationCenterUri).build()
    }

    @ApiOperation(value = "Delete allocation center")
    @DeleteMapping("/{id}")
    fun deleteAllocationCenter(@PathVariable("id") id: Int): ResponseEntity<Void> {
        allocationCenterService.deleteAllocationCenter(id)
        return ResponseEntity.noContent().build()
    }
}
