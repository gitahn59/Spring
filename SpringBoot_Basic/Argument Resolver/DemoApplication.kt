package com.example.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

data class User(val name : String, val age : Int)

annotation class UserBody

@Component
class UserHandlerMethodArgumentResolver : HandlerMethodArgumentResolver{
	override fun supportsParameter(parameter: MethodParameter): Boolean {
		return parameter.hasParameterAnnotation(UserBody::class.java)
	}

	override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? {
		val name = webRequest.getParameter("name") ?: "default"
		val age = webRequest.getParameter("age")?.toInt() ?: 15

		return User(name, age)
	}
}

@Configuration
class WebConfig : WebMvcConfigurer {
	@Autowired
	lateinit var userHandlerMethodArgumentResolver : UserHandlerMethodArgumentResolver

	override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
		resolvers.add(userHandlerMethodArgumentResolver)
	}
}

@RestController
@RequestMapping("/user")
class UserController(){
	@PostMapping("/create")
	fun create(@UserBody user : User) = user
}
