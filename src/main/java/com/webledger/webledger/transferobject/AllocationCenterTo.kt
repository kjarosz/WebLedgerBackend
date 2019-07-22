package com.webledger.webledger.transferobject

import java.math.BigDecimal

data class AllocationCenterTo(
    val id: Int? = null,
    val name: String,
    val goal: BigDecimal,
    val accountId: Int,
    val paidFrom: Int?
)
