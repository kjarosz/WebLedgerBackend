package com.webledger.webledger.repository

import com.webledger.webledger.entity.Transaction
import org.springframework.data.repository.CrudRepository

interface TransactionRepository: CrudRepository<Transaction, Int>
