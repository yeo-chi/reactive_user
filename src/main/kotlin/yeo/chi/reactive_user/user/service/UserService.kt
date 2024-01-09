package yeo.chi.reactive_user.user.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import yeo.chi.reactive_user.user.controller.data.SignInUserRequest
import yeo.chi.reactive_user.user.controller.data.UpdateUserRequest
import yeo.chi.reactive_user.user.persistent.User
import yeo.chi.reactive_user.user.persistent.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,

    private val passwordEncoder: BCryptPasswordEncoder,

    private val transactionalOperator: TransactionalOperator,
) {
    fun getList() = userRepository.findAll().filter(User::isNotLeave)

    fun get(id: Long) = userRepository.findById(id).filter(User::isNotLeave)

    fun create(user: User): Mono<User> {
        return isExists(user.userId)
            .flatMap {
                if (it) {
                    Mono.error(IllegalArgumentException("User ID already exists"))
                } else {
                    save(user)
                }
            }
    }

    private fun isExists(userId: String) = userRepository.existsByUserId(userId)

    private fun save(user: User) = userRepository.save(user)

    fun signIn(request: SignInUserRequest): Mono<User> {
        require(request.userId.isNotEmpty() && request.password.isNotEmpty())

        return userRepository.findByUserId(userId = request.userId)
            .also {
                it.subscribe { user ->
                    user.validPassword(
                        password = request.password,
                        passwordEncoder = passwordEncoder,
                    )
                }
            }
    }

    fun update(id: Long, request: UpdateUserRequest): Mono<Void> {
        return transactionalOperator.execute {
            userRepository.findById(id)
                .filter { it.isNotLeave() }
                .flatMap { user ->
                    user.update(request = request)
                    userRepository.save(user)
                }.then()
        }.next()
    }

    fun delete(id: Long): Mono<Void> {
        return transactionalOperator.execute {
            userRepository.findById(id)
                .filter { it.isNotLeave() }
                .flatMap { user: User ->
                    user.delete()
                    userRepository.save(user)
                }.then()
        }.next()
    }
}
