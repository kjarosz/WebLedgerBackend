package com.webledger.webledger.account

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class AccountController(
        @Autowired
        val accountService: AccountService)
{

    @GetMapping("/accounts")
    fun getAccounts() = accountService.getAllAccounts()

    @PostMapping("/accounts/save")
    fun saveAccount(@RequestBody accountUpdate: AccountUpdate) :Account? {
        val account = Account(
                accountUpdate.id,
                accountUpdate.name!!,
                accountUpdate.type!!,
                BigDecimal.ZERO,
                accountUpdate.limit!!
        )
        return accountService.saveAccount(account)
    }
}
