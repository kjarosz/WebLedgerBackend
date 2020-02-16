package com.webledger.webledger.entity

import java.math.BigDecimal
import javax.persistence.*

@Entity
data class AllocationCenter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(nullable = false)
    var name: String,

    @Column
    var amount: BigDecimal,

    @Column
    var goal: BigDecimal,

    @OneToOne
    var account: Account,

    @OneToOne
    var paidFrom: Account?,

    @OneToMany(mappedBy = "sourceAllocationCenter")
    var sourcesTransactions: List<Transaction>?,

    @OneToMany(mappedBy = "destinationAllocationCenter")
    var destinationTransactions: List<Transaction>?
)