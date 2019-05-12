package com.webledger.webledger.allocationcenter

import com.webledger.webledger.account.Account
import org.springframework.data.repository.CrudRepository

interface AllocationCenterRepository : CrudRepository<Account, Int>
