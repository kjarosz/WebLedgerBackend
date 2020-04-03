package com.webledger.webledger.configuration

import com.webledger.webledger.service.AuthorizationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import javax.servlet.ServletContext

@Configuration
@EnableWebSecurity
open class WebSecurityConfig(
        @Autowired
        private val webLedgerSecurity: WebLedgerSecurity,
        @Autowired
        private val servletContext: ServletContext,
        @Autowired
        private val authorizationService: AuthorizationService
): WebSecurityConfigurerAdapter() {
    private val log: Logger = getLogger(WebSecurityConfig::class.java.simpleName)

    override fun configure(http: HttpSecurity) {
        log.info("Setting up security with WebLedgerSecurity: {}", webLedgerSecurity)
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage(webLedgerSecurity.loginUrl)
                .loginProcessingUrl(servletContext.contextPath + "/login")
                .and()
                .authenticationProvider(authorizationService)
    }
}