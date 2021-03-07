package com.example.demo.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class AuthenticationUser(
        private val username: String,
        private val password : String,
) : AbstractAuthenticationToken(emptyList()) {
    override fun getCredentials(): String? {
        return password
    }

    override fun getPrincipal(): String? {
        return username
    }
}