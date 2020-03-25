package com.webledger.webledger.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigDecimal
import javax.persistence.*

@Entity
data class Account (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int? = null,

        @Column(nullable = false)
        var name: String,

        @Column(nullable = false,
                columnDefinition = "smallint")
        @Enumerated
        var type: AccountType,

        @Column(nullable = false)
        var amount: BigDecimal = BigDecimal.ZERO,

        @Column(name = "credit_limit", nullable = true)
        var limit: BigDecimal,

        @OneToMany(mappedBy="account")
        @JsonIgnore
        var allocationCenters: List<AllocationCenter>
)
