package com.webledger.webledger.configuration

import com.webledger.webledger.service.AuthorizationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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

    override fun configure(http: HttpSecurity) {
        log.info("Setting up security with WebLedgerSecurity: {}", webLedgerSecurity)
        val loginProcessingUrl = servletContext.contextPath + "/login"
        log.info("Login url: {}", loginProcessingUrl)
        http
                .authorizeRequests()
                .antMatchers(servletContext.contextPath + "/**")
                .authenticated()
                .antMatchers(loginProcessingUrl)
                .permitAll()
                .and()
                .formLogin()
                .loginPage(webLedgerSecurity.loginUrl)
                .loginProcessingUrl(loginProcessingUrl)
                .permitAll()
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                //.authenticationProvider(authorizationService)
                .csrf()
                .disable()
    }
}