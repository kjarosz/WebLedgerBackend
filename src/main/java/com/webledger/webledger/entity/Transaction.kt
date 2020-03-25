package com.webledger.webledger.entity

import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.*

@Entity(name = "transactions")
data class Transaction(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int? = null,

        @Column(nullable = false)
        var dateCreated: LocalDate,

        @Column(nullable = false,
                columnDefinition = "smallint")
        @Enumerated
        var transactionType: TransactionType,

        @ManyToOne
        var sourceAllocationCenter: AllocationCenter?,

        @ManyToOne
        var destinationAllocationCenter: AllocationCenter?,

        @Column(nullable = false)
        var amount: BigDecimal,

//    @Column
//    var category: Category,

        @Column
        var dateBankProcessed: LocalDate?,

        @ManyToOne
        var creditAccount: Account?
)