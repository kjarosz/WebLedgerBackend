package com.webledger.webledger.configuration

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
        private val servletContext: ServletContext
): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage(webLedgerSecurity.loginUrl)
    }
}