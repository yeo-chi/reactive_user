package yeo.chi.reactive_user.user.persistent

import java.time.LocalDateTime
import java.time.LocalDateTime.now
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import yeo.chi.reactive_user.user.controller.data.UpdateUserRequest

@Table(name = "\"user\"")
data class User(
    @Id
    val id: Long = 0,

    val userId: String,

    val password: String,

    val userName: String,

    var introduce: String? = null,

    val createdAt: LocalDateTime = now(),

    var updatedAt: LocalDateTime? = null,

    var deletedAt: LocalDateTime? = null,
) {
    fun isNotLeave() = deletedAt == null

    fun validPassword(password: String, passwordEncoder: BCryptPasswordEncoder) {
        require(passwordEncoder.matches(password, this.password)) { "비밀번호가 일치하지 않습니다" }
    }

    fun update(request: UpdateUserRequest) {
        introduce = request.introduce
        updatedAt = now()
    }

    fun delete() {
        deletedAt = now()
    }
}
