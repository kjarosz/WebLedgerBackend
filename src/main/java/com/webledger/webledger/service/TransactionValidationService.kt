package com.webledger.webledger.service

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.exceptions.InvalidAllocationCenters
import com.webledger.webledger.exceptions.MissingCreditAccount
import org.springframework.stereotype.Service

@Service
class TransactionValidationService {
    fun validateTransaction(transaction: Transaction) {
        if (!hasValidAllocationCenters(transaction)) {
            throw InvalidAllocationCenters("Transaction doesn't have a valid allocation centers.")
        }

        if (!hasValidCreditAccount(transaction)) {
            throw MissingCreditAccount("Transaction's credit account is invalid.")
        }
    }

    fun hasValidAllocationCenters(transaction: Transaction): Boolean {
        return (transaction.transactionType == TransactionType.Add && transaction.destinationAllocationCenter != null)
            || (transaction.transactionType == TransactionType.Spend && transaction.sourceAllocationCenter != null)
            || (transaction.transactionType == TransactionType.Transfer && transaction.sourceAllocationCenter != null
                && transaction.destinationAllocationCenter != null)
            || (transaction.transactionType == TransactionType.Credit && transaction.sourceAllocationCenter != null)
    }

    fun hasValidCreditAccount(transaction: Transaction): Boolean {
        return transaction.transactionType != TransactionType.Credit || transaction.creditAccount != null
    }
}