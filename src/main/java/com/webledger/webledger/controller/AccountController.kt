package com.webledger.webledger.controller

import com.webledger.webledger.entity.Account
import com.webledger.webledger.service.AccountService
import com.webledger.webledger.transferobject.AccountTo
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AccountController(
        @Autowired
        val accountService: AccountService)
{
    @ApiOperation(value = "Get list of all accounts", response = Array<Account>::class)
    @GetMapping("/accounts")
    fun getAccounts(): ResponseEntity<Iterable<Account>?> = ResponseEntity.ok(accountService.getAllAccounts())

    @ApiOperation(value = "Get account by id", response = Account::class)
    @GetMapping("/accounts/{id}")
    fun getAccount(@PathVariable("id") id: Int): ResponseEntity<Account?> {
        val account = accountService.getAccount(id)
        return if (account != null) {
            ResponseEntity.ok(account)
        } else {
            ResponseEntity.notFound().build()
        }
    } 

    @ApiOperation(value = "Save an account, new or update", response = Account::class)
    @PostMapping("/accounts/save")
    fun saveAccount(@RequestBody accountTo: AccountTo) :ResponseEntity<Account?> {
        val account = accountService.saveAccount(accountTo)
        return ResponseEntity.ok(account)
    }
}
