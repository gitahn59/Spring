# Handler Method Argument Resolver

Resolver는 앤드포인트로 들어오는 HTTP Request를 파싱해서 자바 오브젝트를 생성하고,   
앤드포인트와 매핑된 핸들러 매소드로 전달한다.

```kotlin
// 예제 1
@PostMapping("/find")
fun getUser(@RequestBody user : User) = user
```

위의 예제에서는 Http Request를 분석하고 User 클래스를 생성해서 getUser 핸들러의
파라미터로 전달해준다.

## 내부 동작
앤드포인트로 요청이 들어오면 boot는 미리 등록되어있는 resolver들을 가져와
그 요청을 지원하는 resolver를 탐색한다. 

```java

public class RequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor {
...
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestBody.class);
    }
...
}
```

예제 1과 같은 경우 RequestResponseBodyMethodProcessor가 parameter가 RequestBody 어노테이션을 가지고 있는지 확인하고,   
boolean 값을 리턴해서 처리 가능여부를 확인한다.
user에는 @RequestBody가 설정되어있고,   
HttpRequest body에 포함된 json 값을 파싱(Jackson을 사용해서)해서 User 오브젝트를 생성해서 handler로 전달해준다.       

## 사용자 정의 리졸버
HandlerMathodArgumentResolver 인터페이스를 구현하면 Custom Resolver를 만들 수 있다.    

```java
public interface HandlerMethodArgumentResolver {
	boolean supportsParameter(MethodParameter parameter);
	@Nullable
	Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception;
```

### User Class를 매핑해주는 Custom Resolver 제작

```kotlin
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
```

User 클래스를 처리하는 CustomResolver는 위의 예제처럼 HandlerMethodArgumentResolver를 구현하면 손쉽게 만들 수 있다.
Handler 메소드의 파라미터에 UserBody가 붙어있으면,   
직접 구현한 UserHandlerMethodArgumentResolver가 post 요청을 파싱해서 User 오브젝트를 생성해서 파라미터에 전달해준다.

이렇게 구현한 Resolver는 Boot 에 등록해야 동작한다.
부트 설정에 등록하기 위해서는 Resolver가 빈으로 등록되어있어야 하므로 @Component를 붙여준다.

### Resolver 등록

이렇게 생성된 Resolver는 WebMvcConfigurer에 등록하여 사용할 수 있다.
```kotlin
@Configuration
class WebConfig : WebMvcConfigurer{
    @Autowired
    lateinit var userHandlerMethodArgumentResolver : UserHandlerMethodArgumentResolver

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userHandlerMethodArgumentResolver)
    }
}
```

@Autowired를 통해 Bean으로 생성된 CustomResolver를 가져오고,   
addArgumentResolvers를 override해서 이 Resolver를 추가적으로 등록한다.

### 결과 확인    

간단한 컨트롤러를 구현해서 결과를 확인해보자.

```kotlin
@RestController
@RequestMapping("/user")
class UserController(){
	@PostMapping("/create")
	fun create(@UserBody user : User) = user
}
```

Post / Form data를 통해 name과 age를 전달하면,   
Custom Resolver를 통해 user 가 생성되고 다시 JSON으로 결과가 리턴된다.   

```json
{
    "name": "alan",
    "age": 27
}
```

