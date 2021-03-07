package com.example.demo.controller

import com.example.demo.entity.User
import com.example.demo.repository.UserRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/register")
class UserRegistrationController(
    private val userRepository: UserRepository
){
    @GetMapping()
    fun register(
            @RequestParam("username") username : String,
            @RequestParam("password") password : String,
    ) : String {
        userRepository.save(User(username, password))
        return "successs"
    }
}