package com.example.demo.security

import com.example.demo.service.UserService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class HeaderAuthenticationProvider (
        private val userService: UserService
): AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        val username = authentication.principal as String
        val password = authentication.credentials as String

        val user = userService.loadUserByUsername(username)
        if(!(user.username == username && user.password == password))
            throw BadCredentialsException(username)
        if(!user.isEnabled)
            throw BadCredentialsException(username)
        return UsernamePasswordAuthenticationToken(username, password)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return true
    }
}