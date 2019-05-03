package com.webledger.webledger

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class WebLedgerApplication

fun main(args: Array<String>) {
    SpringApplication.run(WebLedgerApplication::class.java, *args)
}

