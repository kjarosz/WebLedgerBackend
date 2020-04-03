package com.webledger.webledger.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity(name = "users")
data class User (
        @Id
        val username: String,

        @Column(nullable = false)
        val password: String,

        @Column
        val name: String?,

        @Column
        val enabled: Boolean
)
