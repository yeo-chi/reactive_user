package yeo.chi.reactive_user.user.persistent

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : R2dbcRepository<User, Long> {
    fun findByUserId(userId: String): Mono<User>

    fun existsByUserId(userId: String): Mono<Boolean>
}
