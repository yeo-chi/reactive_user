package yeo.chi.reactive_user.user.controller.data

import java.time.LocalDateTime
import yeo.chi.reactive_user.user.persistent.User

data class UserResponse(
    private val user: User,
) {
    val id: Long

    val userId: String

    val password: String

    val userName: String

    val introduce: String?

    val createdAt: LocalDateTime

    val updatedAt: LocalDateTime?

    val deletedAt: LocalDateTime?

    init {
        id = user.id
        userId = user.userId
        password = user.password
        userName = user.userName
        introduce = user.introduce
        createdAt = user.createdAt
        updatedAt = user.updatedAt
        deletedAt = user.deletedAt
    }
}
