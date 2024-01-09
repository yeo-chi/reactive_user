package yeo.chi.reactive_user.user.controller

import java.util.UUID
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseCookie
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import yeo.chi.reactive_user.user.configuration.jwt.TokenProvider
import yeo.chi.reactive_user.user.controller.data.CreateUserRequest
import yeo.chi.reactive_user.user.controller.data.SignInUserRequest
import yeo.chi.reactive_user.user.controller.data.UpdateUserRequest
import yeo.chi.reactive_user.user.controller.data.UserResponse
import yeo.chi.reactive_user.user.service.UserService

@RestController
@RequestMapping("api/v1/users")
class UserController(
    private val userService: UserService,

    private val passwordEncoder: BCryptPasswordEncoder,

    private val tokenProvider: TokenProvider,
) {
    @GetMapping
    @ResponseStatus(OK)
    fun getList(): Flux<UserResponse> {
        return userService.getList().map(::UserResponse)
    }

    @GetMapping("{id}")
    @ResponseStatus(OK)
    fun get(@PathVariable("id") id: Long): Mono<UserResponse> {
        return userService.get(id = id).map(::UserResponse)
    }

    @PostMapping("signUp")
    @ResponseStatus(CREATED)
    fun create(@RequestBody createUserRequest: CreateUserRequest): Mono<UserResponse> {
        return userService.create(user = createUserRequest.toEntity(passwordEncoder = passwordEncoder))
            .map(::UserResponse)
    }

    @PostMapping("signIn")
    @ResponseStatus(OK)
    fun signIn(
        @RequestBody signInUserRequest: SignInUserRequest,
        serverWebExchange: ServerWebExchange,
    ) {
        val response = serverWebExchange.response
        val uuid = UUID.randomUUID().toString()

        userService.signIn(request = signInUserRequest).let {
            response.headers.add("Authentication", tokenProvider.createToken(it, uuid))
            response.addCookie(
                ResponseCookie.from("identify", uuid)
                    .httpOnly(true)
                    .path("/")
                    .build()
            )
        }
    }

    @PutMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody updateUserRequest: UpdateUserRequest,
    ): Mono<Void> {
        return userService.update(id = id, request = updateUserRequest)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    fun delete(
        @PathVariable("id") id: Long,
        serverWebExchange: ServerWebExchange,
    ): Mono<Void> {
        serverWebExchange.response.addCookie(
            ResponseCookie.from("identify", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build()
        )

        return userService.delete(id = id)
    }
}
