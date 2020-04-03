package com.webledger.webledger.repository

import com.webledger.webledger.entity.Authority
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : CrudRepository<Authority, String> {
    fun findByUsername(username: String): List<Authority>?
}
