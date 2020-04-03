package com.webledger.webledger.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

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
