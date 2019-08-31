package com.webledger.webledger.controller

import com.webledger.webledger.entity.Account
import com.webledger.webledger.service.AccountService
import com.webledger.webledger.transferobject.AccountTo
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.apache.logging.log4j.kotlin.Logging;

@RestController
@CrossOrigin(origins = [ "http://localhost:4200" ])
class AccountController(
        @Autowired
        val accountService: AccountService
): Logging
{
    @ApiOperation(value = "Get list of all accounts", response = Array<Account>::class)
    @GetMapping("/accounts")
    fun getAccounts(): ResponseEntity<Iterable<Account>?> = ResponseEntity.ok(accountService.getAllAccounts())

    @ApiOperation(value = "Get account by id", response = Account::class)
    @GetMapping("/accounts/{id}")
    fun getAccount(@PathVariable("id") id: Int): ResponseEntity<Account?> {
        logger.info { "Fetching account with id: $id" }
        val account = accountService.getAccount(id)
        return if (account != null) {
            logger.debug { "Account fetched: $account " }
            ResponseEntity.ok(account)
        } else {
            ResponseEntity.notFound().build()
        }
    } 

    @ApiOperation(value = "Save an account, new or update", response = Account::class)
    @PostMapping("/accounts/save")
    fun saveAccount(@RequestBody accountTo: AccountTo) :ResponseEntity<Account?> {
        logger.info("Saving account: $accountTo")
        val account = accountService.saveAccount(accountTo)
        logger.debug( "Saved account: $account")
        return ResponseEntity.ok(account)
    }
}
