package com.webledger.webledger.account

import java.math.BigDecimal
import javax.persistence.*

@Entity
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    @Enumerated
    var type: AccountType,

    @Column(nullable = true)
    var limit: BigDecimal
)

enum class AccountType {
    Checking, Savings, Credit, Loan
}
