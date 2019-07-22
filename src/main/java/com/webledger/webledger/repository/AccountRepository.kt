package com.webledger.webledger.repository

import com.webledger.webledger.entity.Account
import org.springframework.data.repository.CrudRepository

interface AccountRepository : CrudRepository<Account, Int>
