package com.webledger.webledger.repository

import com.webledger.webledger.entity.AllocationCenter
import org.springframework.data.repository.CrudRepository

interface AllocationCenterRepository : CrudRepository<AllocationCenter, Int>
