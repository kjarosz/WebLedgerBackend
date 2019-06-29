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
        /*
        if (account.id != null && accountRepository.existsById(account.id)) {
            var storedAccount = accountRepository.findById(account.id).get()
            account.amount = storedAccount.amount
        }
        return accountRepository.save(account)
        */
        val (id, name, type, limit) = accountTo
        val account = Account(id, name, type, BigDecimal.ZERO, limit)
        return accountRepository.save(account)
    }
}