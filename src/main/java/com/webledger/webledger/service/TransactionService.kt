package com.webledger.webledger.service

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.exceptions.TransactionNotFoundException
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.repository.TransactionRepository
import com.webledger.webledger.transferobject.TransactionTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransactionService(
        @Autowired
        val transactionRepository: TransactionRepository,

        @Autowired
        val transactionValidationService: TransactionValidationService,

        @Autowired
        val transactionPropagationService: TransactionPropagationService,

        @Autowired
        val allocationCenterRepository: AllocationCenterRepository
) {

    fun getAllTransactions(): Iterable<Transaction>? = transactionRepository.findAll()

    fun saveTransaction(transactionTo: TransactionTo): Transaction? {
        val transaction = createTransactionFromTo(transactionTo)
        transactionValidationService.validateTransaction(transaction)
        val oldTransaction = getTransaction(transactionTo.id)
        transactionPropagationService.propagateTransactionChanges(transaction, oldTransaction)
        return transactionRepository.save(transaction)
    }

    fun createTransactionFromTo(transactionTo: TransactionTo): Transaction {
        var sourceAllocationCenter: AllocationCenter? = null
        if (transactionTo.sourceAllocationCenterId != null) {
             sourceAllocationCenter = allocationCenterRepository
                    .findById(transactionTo.sourceAllocationCenterId)
                    .orElse(null)
        }

        var destinationAllocationCenter: AllocationCenter? = null
        if (transactionTo.destinationAllocationCenterId != null) {
            destinationAllocationCenter = allocationCenterRepository
                    .findById(transactionTo.destinationAllocationCenterId)
                    .orElse(null)
        }

        return Transaction(
            transactionTo.id,
            transactionTo.dateCreated,
            transactionTo.transactionType,
            sourceAllocationCenter,
            destinationAllocationCenter,
            transactionTo.amount,
            transactionTo.dateBankProcessed,
            null
        )
    }

    fun getTransaction(transactionId: Int?): Transaction? =
        if(transactionId != null)
            transactionRepository.findByIdOrNull(transactionId!!)
        else
            null

    fun deleteTransaction(transactionId: Int) {
        val transaction = getTransaction(transactionId)
                ?: throw TransactionNotFoundException("Transaction for id $transactionId not found")
        val newTransaction = transaction.copy(amount = BigDecimal.ZERO)
        transactionPropagationService.propagateTransactionChanges(newTransaction, transaction)
        transactionRepository.delete(transaction)
    }
}
