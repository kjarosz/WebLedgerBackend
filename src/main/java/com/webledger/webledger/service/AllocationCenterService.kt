package com.webledger.webledger.service

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.exceptions.AccountNotFoundException
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.transferobject.AllocationCenterTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class AllocationCenterService(
        @Autowired
        val accountService: AccountService,

        @Autowired
        val allocationCenterRepository: AllocationCenterRepository
) {
    fun getAllAllocationCenters(): Iterable<AllocationCenter>? = allocationCenterRepository.findAll()

    fun getAllocationCenter(id: Int): AllocationCenter? = allocationCenterRepository.findByIdOrNull(id)

    fun saveAllocationCenter(allocationCenterTo: AllocationCenterTo): AllocationCenter? {
        val (id, name, goal, accountId, paidFrom) = allocationCenterTo
        val account = accountService.getAccount(accountId) ?: throw AccountNotFoundException("Account with ${allocationCenterTo.id} could not be found." )
        return if (id == null || allocationCenterRepository.findById(id) == null) {
            val allocationCenter = AllocationCenter(id, name, BigDecimal.ZERO, goal, account!!, null)
            allocationCenterRepository.save(allocationCenter)
        } else {
            null
        }
    }

    fun updateAllocationCenters(transaction: Transaction) {
        if (transaction.transactionType == TransactionType.Add ||
                transaction.transactionType == TransactionType.Transfer ||
                transaction.transactionType == TransactionType.Credit) {
            transaction.destinationAllocationCenter!!.amount += transaction.amount
        }

        if (transaction.transactionType == TransactionType.Transfer ||
                transaction.transactionType == TransactionType.Spend ||
                transaction.transactionType == TransactionType.Credit ||
                transaction.transactionType == TransactionType.Pay) {
            transaction.sourceAllocationCenter!!.amount -= transaction.amount
        }
    }
}