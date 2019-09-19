package com.webledger.webledger.transferobject

import com.fasterxml.jackson.annotation.JsonFormat
import com.webledger.webledger.entity.Account
import com.webledger.webledger.entity.AllocationCenter
import com.webledger.webledger.entity.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

data class TransactionTo (
        val id: Int? = null,
        @JsonFormat(pattern="yyyy-MM-dd")
        var dateCreated: LocalDate,
        var transactionType: TransactionType,
        var sourceAllocationCenterId: Int?,
        var destinationAllocationCenterId: Int?,
        var amount: BigDecimal,
//    var category: Category,
        @JsonFormat(pattern="yyyy-MM-dd")
        var dateBankProcessed: LocalDate?,
        var creditAccountId: Int?
)
