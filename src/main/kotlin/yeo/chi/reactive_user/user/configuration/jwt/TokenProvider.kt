package yeo.chi.reactive_user.user.configuration.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime.now
import java.util.*
import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import yeo.chi.reactive_user.user.persistent.User

@Component
class TokenProvider(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,

    @Value("\${jwt.issuer}")
    private val issuer: String,

    @Value("\${jwt.expiration-second}")
    private val expirationSecond: Long,
) {
    fun createToken(user: Mono<User>, uuid: String): String {
        return user.let {
            Jwts.builder()
                .subject(getIdentificationInformation(it))
                .claim("identify", uuid)
                .issuer(issuer)
                .issuedAt(Timestamp.valueOf(now()))
                .expiration(Date.from(Instant.now().plusSeconds(expirationSecond)))
                .signWith(SecretKeySpec(secretKey.toByteArray(), SignatureAlgorithm.HS512.jcaName))
                .compact()!!
        }
    }

    private fun getIdentificationInformation(user: Mono<User>): String {
        var userId = 0L
        user.map { userId = it.id }

        return "${userId}:${Role.USER.securityRole}"
    }


    fun getSubject(token: String, identify: String): String {
        return getClaims(token = token).also {
            check(it["identify"].toString() == identify)
        }.subject
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(SecretKeySpec(secretKey.toByteArray(), SignatureAlgorithm.HS512.jcaName))
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
