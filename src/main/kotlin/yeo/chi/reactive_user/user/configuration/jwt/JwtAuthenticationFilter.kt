package yeo.chi.reactive_user.user.configuration.jwt

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import yeo.chi.reactive_user.user.configuration.jwt.Role.ANONYMOUS

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
) : WebFilter {
    private val IDENTIFY: String = "identify"

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        val token = resolveToken(exchange.request)
        if (token == ANONYMOUS.name) {
            val user = User(
                ANONYMOUS.name,
                "",
                listOf(SimpleGrantedAuthority(ANONYMOUS.securityRole)),
            )

            return UsernamePasswordAuthenticationToken(user, token, user.authorities).run {
                chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(this))
            }
        }

        val identify = getCookieValueByKey(exchange = exchange)
        val user = parseIdentificationInformation(token, identify)

        return UsernamePasswordAuthenticationToken(user, token, user.authorities).run {
            chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(this))
        }
    }

    private fun resolveToken(request: ServerHttpRequest): String {
        return request.headers.getFirst(AUTHORIZATION)
            ?.substring(7)
            ?: ANONYMOUS.name
    }

    private fun getCookieValueByKey(exchange: ServerWebExchange): String {
        return exchange.request.cookies.getFirst(IDENTIFY)
            ?.value
            ?: throw NullPointerException()
    }

    private fun parseIdentificationInformation(token: String, identify: String): User {
        return tokenProvider.getSubject(token = token, identify = identify)
            .split(":")
            .let { User(it[0], "", listOf(SimpleGrantedAuthority(it[1]))) }
    }
}
