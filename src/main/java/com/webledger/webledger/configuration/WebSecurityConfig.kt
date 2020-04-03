package com.webledger.webledger.configuration

import com.webledger.webledger.service.AuthorizationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import javax.servlet.ServletContext
import javax.sql.DataSource

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

    private val usersByUsernameQuery =
            "select username, password, enabled from users where lower(username) = lower(?)"
    private val authoritiesByUsernameQuery =
            "select username, authority from authorities where lower(username) = lower(?)"

    @Autowired
    fun initialize(builder: AuthenticationManagerBuilder, dataSource: DataSource) {
        log.info("Initializing JDBC Authentication")
        builder.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(usersByUsernameQuery)
                .authoritiesByUsernameQuery(authoritiesByUsernameQuery)
                .passwordEncoder(BCryptPasswordEncoder())
    }

    @Bean
    open fun jdbcUserDetailsManager(dataSource: DataSource): JdbcUserDetailsManager {
        val jdbcUserDetailsManager = JdbcUserDetailsManager()
        jdbcUserDetailsManager.dataSource = dataSource
        return jdbcUserDetailsManager
    }

    override fun configure(http: HttpSecurity) {
        log.info("Setting up security with WebLedgerSecurity: {}", webLedgerSecurity)
        if (webLedgerSecurity.enabled) {
            log.info("Securing endpoints at context-path: {}", servletContext.contextPath)
            val loginProcessingUrl = servletContext.contextPath + "/login"
            http.authorizeRequests().antMatchers(servletContext.contextPath + "/**").authenticated().antMatchers(
                    loginProcessingUrl).permitAll().and().formLogin().loginPage(
                    webLedgerSecurity.loginUrl).loginProcessingUrl(loginProcessingUrl).permitAll().usernameParameter(
                    "username").passwordParameter("password").and()
                    .csrf().disable()
        } else {
            log.info("Security disabled")
            http.authorizeRequests()
                    .anyRequest()
                    .permitAll()
                    .and()
                    .csrf()
                    .disable()
        }
    }
}