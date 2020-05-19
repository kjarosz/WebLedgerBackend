package com.webledger.webledger

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession

@SpringBootApplication
@EnableJdbcHttpSession
open class WebLedgerApplication

fun main(args: Array<String>) {
    SpringApplication.run(WebLedgerApplication::class.java, *args)
}

