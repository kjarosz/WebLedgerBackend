package com.webledger.webledger.service

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.exceptions.InvalidAllocationCenters
import com.webledger.webledger.exceptions.MissingCreditAccount
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.repository.TransactionRepository
import com.webledger.webledger.transferobject.TransactionTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionService(
        @Autowired
        val transactionRepository: TransactionRepository,

        @Autowired
        val transactionValidationService: TransactionValidationService,

        @Autowired
        val allocationCenterRepository: AllocationCenterRepository,

        @Autowired
        val allocationCenterService: AllocationCenterService
) {
    fun saveTransaction(transactionTo: TransactionTo): Transaction? {
        val (id, dateCreated, transactionType, sourceAllocationCenterId, destinationAllocationCenterId, amount, dateBankProcessed, creditAccount) = transactionTo

        val sourceAllocationCenter = allocationCenterRepository.findById(sourceAllocationCenterId).orElse(null)
        val destinationAllocationCenter = allocationCenterRepository.findById(destinationAllocationCenterId).orElse(null)

        val transaction = Transaction(id, dateCreated, transactionType,
                sourceAllocationCenter, destinationAllocationCenter,
                amount, dateBankProcessed, creditAccount)

        if (!transactionValidationService.hasValidAllocationCenters(transaction)) {
            throw InvalidAllocationCenters("Transaction doesn't have a valid allocation centers.")
        }

        if (!transactionValidationService.hasValidCreditAccount(transaction)) {
            throw MissingCreditAccount("Transaction's credit account is invalid.")
        }

        updateAllocationCenters(transaction)
        updateAccounts(transaction)

        return transactionRepository.save(transaction)
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
