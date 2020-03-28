package com.webledger.webledger.repository

import com.webledger.webledger.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, String>  {
    fun findByUsername(username: String): User?
}
