package com.webledger.webledger.transferobject

data class UserTo (
        val username: String,
        val password: String,
        val name: String,
        val authorities: List<String>
)