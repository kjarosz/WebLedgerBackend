package com.webledger.webledger.entity

import javax.persistence.*

@Entity(name = "authorities")
@IdClass(AuthorityId::class)
data class Authority (
        @Id
        val username: String = "",

        @Id
        val authority: String = ""
)

class AuthorityId(
        @Id
        val username: String = "",

        @Id
        val authority: String = ""
): java.io.Serializable
