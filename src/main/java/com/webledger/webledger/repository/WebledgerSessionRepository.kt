package com.webledger.webledger.repository

import com.webledger.webledger.entity.WebledgerSession
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WebledgerSessionRepository : CrudRepository<WebledgerSession, UUID>
