package com.webledger.webledger.service;

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import org.springframework.stereotype.Service;

@Service
public class TransactionPropagationService {
    fun propagateTransactionChanges(transaction: Transaction, oldTransaction: Transaction?) {
        updateAllocationCenters(transaction)
        updateAccounts(transaction)
    }

    fun updateAllocationCenters(transaction: Transaction) {
        if (addsToDestination(transaction)) {
            transaction.destinationAllocationCenter!!.amount += transaction.amount
        }

        if (takesFromSource(transaction)) {
            transaction.sourceAllocationCenter!!.amount -= transaction.amount
        }
    }

    fun updateAccounts(transaction: Transaction) {
        if (addsToDestination(transaction)) {
            transaction.destinationAllocationCenter!!.account.amount += transaction.amount
        }

        if (takesFromSource(transaction)) {
            transaction.sourceAllocationCenter!!.account.amount -= transaction.amount
        }
    }

    fun reverseUpdateAccount(transaction: Transaction?) {
        if (transaction != null) {
            if (addsToDestination(transaction)) {
                transaction.destinationAllocationCenter!!.account.amount -= transaction.amount
            }

            if (takesFromSource(transaction)) {
                transaction.sourceAllocationCenter!!.account.amount += transaction.amount
            }
        }
    }

    fun addsToDestination(transaction: Transaction): Boolean {
        return transaction.transactionType == TransactionType.Add
                || transaction.transactionType == TransactionType.Transfer
                || transaction.transactionType == TransactionType.Credit
    }

    fun takesFromSource(transaction: Transaction): Boolean {
        return transaction.transactionType == TransactionType.Transfer
                || transaction.transactionType == TransactionType.Spend
                || transaction.transactionType == TransactionType.Credit
                || transaction.transactionType == TransactionType.Pay
    }
}
