package yeo.chi.reactive_user.user.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import yeo.chi.reactive_user.user.configuration.jwt.JwtAuthenticationFilter
import yeo.chi.reactive_user.user.configuration.jwt.TokenProvider

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration(
    private val tokenProvider: TokenProvider,
) {
    @Bean
    fun filterChain(httpSecurity: ServerHttpSecurity): SecurityWebFilterChain {
        return httpSecurity
            .csrf { it.disable() }
            .authorizeExchange {
                it.pathMatchers(
                    "/",
                    "/api/v1/users/signIn",
                    "/api/v1/users/signUp",
                    "/api/v1/users/refresh",
                ).hasRole("ANONYMOUS")
                it.anyExchange().hasRole("USER")
            }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .addFilterAt(
                JwtAuthenticationFilter(tokenProvider = tokenProvider),
                SecurityWebFiltersOrder.HTTP_BASIC,
            )
            .build()
    }

    @Bean
    fun bCryptPasswordEncoder() = BCryptPasswordEncoder()
}
