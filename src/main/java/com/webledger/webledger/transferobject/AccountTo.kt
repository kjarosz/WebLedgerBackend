package com.webledger.webledger.transferobject

import com.webledger.webledger.entity.AccountType
import java.math.BigDecimal

data class AccountTo(
        val id: Int? = null,
        val name: String,
        val type: AccountType,
        val limit: BigDecimal
)