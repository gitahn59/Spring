package com.example.demo.filter

import com.example.demo.security.AuthenticationUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HeaderAuthenticationFilter : OncePerRequestFilter(){
    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        SecurityContextHolder.getContext().apply {
            authentication = AuthenticationUser(
                    request.getHeader("username")?:"null",
                    request.getHeader("password")?:"null"
            )
        }
        filterChain.doFilter(request, response)
    }
}