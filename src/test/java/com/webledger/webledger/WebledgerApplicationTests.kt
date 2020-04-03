package com.webledger.webledger

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner

@ExtendWith(SpringExtension::class)
@SpringBootTest
class WebledgerApplicationTests {
    @Test
    fun contextLoads() {
        fail { "testing failure" }
    }
}
