package com.webledger.webledger.service

import com.webledger.webledger.entity.Account
import com.webledger.webledger.controller.GlobalExceptionHandler.AccountNotFoundException
import com.webledger.webledger.exceptions.DeleteEntityWithChildrenException
import com.webledger.webledger.repository.AccountRepository
import com.webledger.webledger.transferobject.AccountTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class AccountService(
        @Autowired
        val accountRepository: AccountRepository
) {
    fun getAllAccounts(): Iterable<Account>? = accountRepository.findAll()

    fun getAccount(id: Int) = accountRepository.findByIdOrNull(id)

    fun saveAccount(accountTo: AccountTo): Account? {
        val (id, name, type, limit) = accountTo
        var account = if (id != null && accountRepository.existsById(id)) {
            var storedAccount = accountRepository.findById(id).get()
            storedAccount.name = accountTo.name
            storedAccount.type = accountTo.type
            storedAccount.limit = accountTo.limit
            storedAccount
        } else {
            Account(id, name, type, BigDecimal.ZERO, limit, emptyList())
        }
        return accountRepository.save(account)
    }

    fun deleteAccount(id: Int) {
        val account = accountRepository.findByIdOrNull(id)
                ?: throw AccountNotFoundException("Could not find account for id $id")
        if (account.allocationCenters.isNotEmpty()) {
            throw DeleteEntityWithChildrenException("Cannot delete account with id $id because of associated allocation centers")
        }
        accountRepository.delete(account)
    }
}