package com.webledger.webledger.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigDecimal
import javax.persistence.*

@Entity(name = "users")
data class User (
        @Id
        val username: String,

        @Column(nullable = false,
                columnDefinition = "bytea")
        val password: String,

        @Column
        val name: String?

)
