package com.webledger.webledger.controller

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.service.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = [ "http://localhost:4200" ])
class TransactionController(
        @Autowired
        val transactionService: TransactionService
) {

    @GetMapping("/transactions")
    fun getAllTransactions(): ResponseEntity<Iterable<Transaction>?> {
        return ResponseEntity.ok(transactionService.getAllTransactions())
    }
}
