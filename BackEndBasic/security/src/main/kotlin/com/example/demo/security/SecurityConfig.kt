package com.example.demo.security

import com.example.demo.filter.HeaderAuthenticationFilter
import com.example.demo.service.UserService
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@EnableWebSecurity
class SecurityConfig(
        private val cookieAuthenticationProvider: HeaderAuthenticationProvider
) : WebSecurityConfigurerAdapter() {
    override fun configure(web: WebSecurity) {
        web.ignoring()
                .antMatchers("/resources/**")
    }

    override fun configure(http: HttpSecurity) {
        http
                .httpBasic().disable()
                .csrf().disable()
                .headers().frameOptions().disable()

                .and()
                .authorizeRequests()
                .anyRequest().authenticated()

                .and()
                .addFilterBefore(
                        HeaderAuthenticationFilter(), BasicAuthenticationFilter::class.java
                )
                .formLogin().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(cookieAuthenticationProvider)
    }
}

