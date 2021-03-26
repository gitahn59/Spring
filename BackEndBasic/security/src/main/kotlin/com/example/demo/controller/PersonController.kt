package com.example.demo.controller

import com.example.demo.service.PersonService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/person")
class PersonController(
        private val personService: PersonService
){
    @GetMapping
    fun temp() = personService.getTemp()
}