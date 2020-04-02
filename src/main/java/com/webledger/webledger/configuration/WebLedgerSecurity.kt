package com.webledger.webledger.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "webledger.security")
open class WebLedgerSecurity {
    lateinit var loginUrl: String
}
