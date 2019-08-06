package com.webledger.webledger.service

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType

class TransactionValidationService {
    fun hasValidAllocationCenters(transaction: Transaction): Boolean {
        return (transaction.transactionType == TransactionType.Add && transaction.destinationAllocationCenter != null)
            || (transaction.transactionType == TransactionType.Spend && transaction.sourceAllocationCenter != null)
            || (transaction.transactionType == TransactionType.Transfer && transaction.sourceAllocationCenter != null
                && transaction.destinationAllocationCenter != null)
            || (transaction.transactionType == TransactionType.Credit && transaction.sourceAllocationCenter != null)
    }
}