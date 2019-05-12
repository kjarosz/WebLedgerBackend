package com.webledger.webledger.account

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountService(
        @Autowired
        val accountRepository: AccountRepository
) {
    fun getAllAccounts(): Iterable<Account>? = accountRepository.findAll()

    fun saveAccount(account: Account): Account? =
        when {
            account.id == null -> accountRepository.save(account)
            !accountRepository.existsById(account.id) -> accountRepository.save(account)
            else -> null
        }
}