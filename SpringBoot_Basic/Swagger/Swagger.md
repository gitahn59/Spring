# Swagger

스웨거는 개발자가 REST 웹서비스를 설계, 빌드, 문서화, 소바하는 일을 도화주는 프레임워크이다.   
배부분의 사용자들을 스웨거 UI 도구를 통해 스웨거를 식별하며 자동화된 문서화, 코드 생성,   
테스트 케이스 생성 지원을 포함한다.

```kotlin
implementation("org.springdoc:springdoc-openapi-ui:1.4.3")
```

## @Tag
controller에 대해 원하는 이름과 설명을 붙일 수 있는 어노테이션

```kotlin
@Tag(name="User 생성 api", description="나의 User 생성 api")
@RestController
@RequestMapping("/user")
class UserController(){}
```

> @Tag를 사용하지 않으면 컨트롤러 클래스의 이름을 변형시켜 사용한다.

## @Operation

핸들러에 @Operation 어노테이션을 통해 부연 설명을 추가할 수 있다.

```kotlin
data class User(val name : String, val age : Int)
```

Post로 User 클래스에 대한 정보를 받아 User 클래스를 생성하는 핸들러를 구현한다고 가정하자.  
그러면 Json을 통해 name과 age를 전달하거나, query를 통해 전달할 수 있다.

### query
```kotlin
@Operation(
        summary = "user 생성",
        parameters = [
                Parameter(
                        name = "name",
                        description = "유저의 이름",
                        example = "alan",
                        required = true,
                        `in` = ParameterIn.QUERY
                ),
                Parameter(
                        name = "age",
                        description = "유저의 나이",
                        example = "30",
                        required = true,
                        `in` = ParameterIn.QUERY
                )
        ]
)
@PostMapping("/create")
fun create(@UserBody user : User) = user
```

1. summary : 핸들러의 요약 설명을 나타낸다.
2. description : 핸들러에 대해 설명한다.   
3. Parameters : Http 메소드에서 사용할 parameters를 작성한다. 
여러 개가 사용될 수 있으므로 []로 전달받는다.

> `in` : 파라미터가 사용되는 방법을 결정한다.   
> QUERY, HEADER, COOKIE, PATH, DEFAUT 를 고를 수 있다.   
> QUERY 는 일반적으로 POST FORM 을 통해 전달되는 데이터를 포함한다.

### Json

```kotlin
@Operation(
        summary = "json을 사용한 create",
        description = "json을 사용해서 create 할거니까 준비해야한다."
)
@PostMapping("/jcreate")
fun createWithJson(@RequestBody user : User) = user
```

json을 사용할 때는 RequestBody를 사용하므로 별도로 파라미터를 지정하지 않는다.
Swagger는 @RequestBody 어노테이션이 사용되면 자동으로 RequestBody 입력창을 제공하고
Class 타입에 맞는Json 오브젝트를 미리(속성명, 기본값) 입력해준다.   

## @ApiResponses
@Operation이 핸들러를 사용하는 방법을 나타낸다면,    
@ApiResponses는 이름 그대로 핸들러의 결과들을 정리해서 보여줄 수 있다.    

```kotlin
@ApiResponses(
        ApiResponse(responseCode = "201",
                content = [
                    Content(schema = Schema(implementation = User::class),
                            mediaType = "application/json",
                            examples = [
                                ExampleObject(name="Alan을 생성한 경우", value="""
                                    {
                                        "name": "alan",
                                        "age": 27
                                    }
                                """),
                                ExampleObject(name="Blan을 생성한 경우", value="""
                                    {
                                        "name": "Blan",
                                        "age": 28
                                    }
                                """),
                            ]
                    )
                ]),
)
@PostMapping("/jcreate")
fun createWithJson(@RequestBody user : User) = user
```

@ApiResponses 안에 가능한 응답의 종류만큼 @ApiResponse를 지정하여 각 각 결과를 표현한다.   
### @ApiResponse 
1. responseCode : 결과 코드
2. content : 결과의 schema, 예시의 종류, mediaType등을 지정 

#### @ExampleObject
@Content 안에서 원하는 만큼 예시를 표시해 둘 수 있다.


## Swagger 전체 설정

Swagger는 여러 개의 API들에 대해 그룹화하여 별도 페이지로 구분할 수 있다.

```kotlin
@Configuration
@OpenAPIDefinition(info = Info(title="alan's api",description = "alan의 PAPI doc"))
class SwaggerConfig{
    @Bean
    fun userApi() : GroupedOpenApi?{
        return GroupedOpenApi
                .builder()
                .group("user api")
                .pathsToMatch("/user/**")
                .build()
    }

    @Bean
    fun greetingApi() : GroupedOpenApi?{
        return GroupedOpenApi
                .builder()
                .group("greeting api")
                .pathsToMatch("/greeting/**")
                .build()
    }
}
```

먼저 swagger 설정을 담을 @Configuration class를 생성한다.   
@OpenAPIDefination 어노테이션으로 전체 API에 대한 요약내용과 설명을 정의한다.

그리고 나눌 그룹에 따라 Bean을 생성한다.
그룹은 GroupedOpenApi 클래스로 표현되며 빌더를 사용해 생성한다.
1. group() : 그룹의 이름을 표시한다. 이를 기준으로 페이지가 별도로 구성된다.
2. pathsToMatch("paths") : 그룹에 포함할 endpoint 경로를 나타낸다.
3. build() : 오브젝트를 빌드한다. 

## @Parameter
@Parameter는 @Operation에서 파라미터 정보를 나타낼 수 있다.   
하지만 직접 handler의 파라미터 앞에 붙어 그 파라미터의 설명을 제공할 수도 있다.   

ex) hidden = true : swagger에서 해당 파라미터를 보여주지 않는다.