package com.webledger.webledger.controller

import com.webledger.webledger.entity.Account
import com.webledger.webledger.entity.AccountType
import com.webledger.webledger.service.AccountService
import com.webledger.webledger.transferobject.AccountTo
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.apache.logging.log4j.kotlin.Logging;

@RestController
@CrossOrigin(origins = [ "http://localhost:4200" ])
@RequestMapping("/accounts")
class AccountController(
        @Autowired
        val accountService: AccountService
): Logging
{
    @ApiOperation(value = "Get list of all accounts", response = Array<Account>::class)
    @GetMapping("/")
    fun getAccounts(): ResponseEntity<Iterable<Account>?> = ResponseEntity.ok(accountService.getAllAccounts())

    @ApiOperation(value = "Get account by id", response = Account::class)
    @GetMapping("/{id}")
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
    @PostMapping
    fun saveAccount(@RequestBody accountTo: AccountTo) :ResponseEntity<Account?> {
        logger.info("Saving account: $accountTo")
        val account = accountService.saveAccount(accountTo)
        logger.debug( "Saved account: $account")
        return ResponseEntity.ok(account)
    }

    @ApiOperation(value = "Get AccountType enum list", response = Array<AccountType>::class)
    @GetMapping("/types")
    fun getAccountTypes(): ResponseEntity<Array<AccountType>> = ResponseEntity.ok(AccountType.values())

    @ApiOperation(value = "Delete an account")
    @DeleteMapping("/{id}")
    fun deleteAccount(@PathVariable("id") id: Int): ResponseEntity<Void> {
        logger.info("Deleting account: $id")
        accountService.deleteAccount(id)
        logger.info("Account deleted: $id")
        return ResponseEntity.noContent().build()
    }
}
