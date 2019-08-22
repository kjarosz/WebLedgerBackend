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

        allocationCenterService.updateAllocationCenters(transaction)

        return transactionRepository.save(transaction)
    }

}
