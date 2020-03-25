package com.webledger.webledger.entity

import com.fasterxml.jackson.annotation.JsonIgnore
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

    @ManyToOne
    var account: Account,

    @ManyToOne
    var paidFrom: Account?,

    @OneToMany(mappedBy = "sourceAllocationCenter")
    @JsonIgnore
    var sourcesTransactions: List<Transaction>?,

    @OneToMany(mappedBy = "destinationAllocationCenter")
    @JsonIgnore
    var destinationTransactions: List<Transaction>?
)