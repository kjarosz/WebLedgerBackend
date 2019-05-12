package com.webledger.webledger.account

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
        @Autowired
        val accountRepository: AccountRepository)
{

    @GetMapping("/accounts")
    fun getAccounts(): Iterable<Account>? = accountRepository.findAll()

    @PostMapping("/accounts")
    fun postAccounts(@RequestBody account: Account) {

        accountRepository.save(account)
    }
}
