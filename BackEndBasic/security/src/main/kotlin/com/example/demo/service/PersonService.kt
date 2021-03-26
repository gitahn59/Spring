package com.example.demo.service

import com.example.demo.entity.Person
import org.springframework.stereotype.Service

@Service
class PersonService(){
    fun getTemp() = Person("alan", 27)
}