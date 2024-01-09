package yeo.chi.reactive_user.user.controller.data

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import yeo.chi.reactive_user.user.persistent.User

data class CreateUserRequest(
    val userId: String,

    val password: String,

    val rePassword: String,

    val userName: String,

    val introduce: String?,
) {
    init {
        require(password == rePassword)
    }

    fun toEntity(passwordEncoder: BCryptPasswordEncoder): User {
        return User(
            userId = userId,
            password = passwordEncoder.encode(password),
            userName = userName,
            introduce = introduce,
        )
    }
}
