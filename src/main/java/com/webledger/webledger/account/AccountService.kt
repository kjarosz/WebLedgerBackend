package com.webledger.webledger.account

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
            Account(id, name, type, BigDecimal.ZERO, limit)
        }
        return accountRepository.save(account)
    }
}