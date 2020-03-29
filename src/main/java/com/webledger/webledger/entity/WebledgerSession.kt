package com.webledger.webledger.entity

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
data class WebledgerSession (
        @Id
        val sessionId: UUID,

        @OneToOne
        @MapsId(value = "username")
        @JoinColumn(name = "username", referencedColumnName = "username")
        val user: User,

        @Column
        val expires: LocalDateTime
)
