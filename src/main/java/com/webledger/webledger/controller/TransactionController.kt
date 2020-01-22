package com.webledger.webledger.controller

import com.webledger.webledger.entity.Transaction
import com.webledger.webledger.entity.TransactionType
import com.webledger.webledger.service.TransactionService
import com.webledger.webledger.transferobject.TransactionTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/transactions/types")
    fun getTransactionTypes(): ResponseEntity<Array<TransactionType>> {
        return ResponseEntity.ok(TransactionType.values())
    }

    @GetMapping("/transactions/{id}")
    fun getTransaction(@PathVariable("id") transactionId: Int): ResponseEntity<Transaction> {
        val transaction = transactionService.getTransaction(transactionId)
        return if (transaction == null)
            ResponseEntity.notFound().build()
        else
            ResponseEntity.ok(transaction)
    }

    @PostMapping("/transactions/save")
    fun saveTransaction(@RequestBody transactionTo: TransactionTo): ResponseEntity<Transaction> {
        return ResponseEntity.ok(transactionService.saveTransaction(transactionTo))
    }

    @DeleteMapping("/transactions/{id}")
    fun deleteTransaction(@PathVariable("id") transactionId: Int): ResponseEntity<String> {
        transactionService.deleteTransaction(transactionId)
        return ResponseEntity.noContent().build()
    }
}
