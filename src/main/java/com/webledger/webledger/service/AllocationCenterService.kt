package com.webledger.webledger.service

import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.exceptions.AccountNotFoundException
import com.webledger.webledger.exceptions.AllocationCenterNotFoundException
import com.webledger.webledger.exceptions.DeleteEntityWithChildrenException
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.transferobject.AllocationCenterTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class AllocationCenterService(@Autowired val accountService: AccountService,

                              @Autowired val allocationCenterRepository: AllocationCenterRepository) {
    fun getAllAllocationCenters(): Iterable<AllocationCenter>? = allocationCenterRepository.findAll()

    fun getAllocationCenter(id: Int): AllocationCenter? = allocationCenterRepository.findByIdOrNull(id)

    fun saveAllocationCenter(allocationCenterTo: AllocationCenterTo): AllocationCenter? {
        val (id, name, goal, accountId) = allocationCenterTo
        val account = accountService.getAccount(accountId)
        return if (account == null) {
            throw AccountNotFoundException("Account with ${allocationCenterTo.id} could not be found.")
        } else if (id == null || !allocationCenterRepository.findById(id).isPresent) {
            val allocationCenter = AllocationCenter(id, name, BigDecimal.ZERO, goal, account, null, null, null)
            allocationCenterRepository.save(allocationCenter)
        } else {
            null
        }
    }

    fun deleteAllocationCenter(id: Int) {
        val allocationCenter = allocationCenterRepository.findByIdOrNull(id) ?: throw AllocationCenterNotFoundException(
                "Allocation center with id $id could not be found")
        if (!allocationCenter.sourcesTransactions.isNullOrEmpty() || !allocationCenter.destinationTransactions.isNullOrEmpty()) {
            throw DeleteEntityWithChildrenException(
                    "Allocation center with id $id has transactions associated with it and cannot be deleted")
        }
        allocationCenterRepository.delete(allocationCenter)
    }
}