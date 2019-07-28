package com.webledger.webledger.service

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.repository.TransactionRepository
import com.webledger.webledger.transferobject.TransactionTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionService(
        @Autowired
        val transactionRepository: TransactionRepository
) {
        fun saveTransaction(transactionTo: TransactionTo): Transaction? {
            return null
        }
}
