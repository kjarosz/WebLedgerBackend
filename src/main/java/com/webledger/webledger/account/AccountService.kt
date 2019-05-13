package com.webledger.webledger.account

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountService(
        @Autowired
        val accountRepository: AccountRepository
) {
    fun getAllAccounts(): Iterable<Account>? = accountRepository.findAll()

    fun saveAccount(account: Account): Account? {
        if (account.id != null && accountRepository.existsById(account.id)) {
            var storedAccount = accountRepository.findById(account.id).get()
            account.amount = storedAccount.amount
        }
        return accountRepository.save(account)
    }
}