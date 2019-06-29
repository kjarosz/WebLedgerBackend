package com.webledger.webledger.account

import java.math.BigDecimal

data class AccountTo(
        val accountId: Int? = null,
        val name: String,
        val type: AccountType,
        val limit: BigDecimal
)