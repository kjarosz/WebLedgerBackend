package com.webledger.webledger.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.properties.Delegates

@Configuration
@ConfigurationProperties(prefix = "webledger.security")
open class WebLedgerSecurityConfig {
    var enabled by Delegates.notNull<Boolean>()
    lateinit var loginUrl: String
    lateinit var successUrl: String
}
