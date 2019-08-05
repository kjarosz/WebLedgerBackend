package com.webledger.webledger.service

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.repository.TransactionRepository
import com.webledger.webledger.transferobject.TransactionTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TransactionService(
        @Autowired
        val transactionRepository: TransactionRepository,

        @Autowired
        val allocationCenterRepository: AllocationCenterRepository
) {
    fun saveTransaction(transactionTo: TransactionTo): Transaction? {
        val (id, dateCreated, transactionType, sourceAllocationCenterId, destinationAllocationCenterId, amount, dateBankProcessed, creditAccount) = transactionTo
        return if (hasValidAllocationCenters(transactionTo)) {
            val sourceAllocationCenter = allocationCenterRepository.findById(sourceAllocationCenterId).orElse(null)
            val destinationAllocationCenter = allocationCenterRepository.findById(destinationAllocationCenterId).orElse(null)
            val transaction = Transaction(id, dateCreated, transactionType,
                    sourceAllocationCenter, destinationAllocationCenter,
                    amount, dateBankProcessed, creditAccount)
            transactionRepository.save(transaction)
        } else {
            null
        }
    }

    fun hasValidAllocationCenters(transactionTo: TransactionTo): Boolean {
        return false
    }
}
