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
import java.math.BigDecimal
import java.time.LocalDate

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
        transactionPropagationService.propagateTransactionChanges(transaction)
        return transactionRepository.save(transaction)
    }

    fun createTransactionFromTo(transactionTo: TransactionTo): Transaction {
        val sourceAllocationCenter = allocationCenterRepository
                .findById(transactionTo.sourceAllocationCenterId)
                .orElse(null)

        val destinationAllocationCenter = allocationCenterRepository
                .findById(transactionTo.destinationAllocationCenterId)
                .orElse(null)

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
}
