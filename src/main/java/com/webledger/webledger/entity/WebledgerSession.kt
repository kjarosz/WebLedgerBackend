package com.webledger.webledger.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
data class WebledgerSession (
        @Id
        val sessionId: UUID,

        @Column
        val user: User,

        @Column
        val expires: LocalDateTime
)
