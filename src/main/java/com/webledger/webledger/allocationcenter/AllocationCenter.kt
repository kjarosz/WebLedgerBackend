package com.webledger.webledger.allocationcenter

import java.math.BigDecimal
import javax.persistence.*

@Entity
data class AllocationCenter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false)
    var name: String,

    @Column
    var amount: BigDecimal,



)