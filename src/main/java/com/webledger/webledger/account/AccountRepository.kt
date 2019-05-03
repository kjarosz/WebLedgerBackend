package com.webledger.webledger.account

import org.springframework.data.repository.CrudRepository

interface AccountRepository : CrudRepository<Account, Int>
