package com.dsm.domain.auth.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.dsm.plugins.SecurityProperties
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID

/**
 *
 * Jwt 토큰을 생성하는 GenerateToken
 *
 * @author Chokyunghyeon
 * @date 2023/03/20
 **/
@Serializable
data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
    @Contextual
    val accessTokenExpired: LocalDateTime
)

class JwtGenerator(
    private val securityProperties: SecurityProperties
) : TokenProvider {

    private fun generateRefreshToken(): String {
        return JWT.create()
            .withSubject(JWT_SUBJECT)
            .withKeyId(JWT_REFRESH)
            .withAudience(securityProperties.audience)
            .withIssuer(securityProperties.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + securityProperties.refreshExpired))
            .sign(Algorithm.HMAC256(securityProperties.secret))
    }

    private fun generateAccessToken(studentId: UUID): String {
        return JWT.create()
            .withSubject(JWT_SUBJECT)
            .withKeyId(JWT_ACCESS)
            .withAudience(securityProperties.audience)
            .withIssuer(securityProperties.issuer)
            .withClaim(JWT_STUDENT_ID, studentId.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + securityProperties.accessExpired))
            .sign(Algorithm.HMAC256(securityProperties.secret))
    }

    override suspend fun generateToken(studentId: UUID): TokenResult {
        return TokenResult(
            accessToken = generateAccessToken(studentId),
            refreshToken = generateRefreshToken(),
            accessTokenExpired = LocalDateTime.now().plusSeconds(securityProperties.accessExpired)
        )
    }

    companion object {
        const val JWT_SUBJECT = "Authentication"
        const val JWT_REFRESH = "Refresh"
        const val JWT_ACCESS = "Access"
        const val JWT_STUDENT_ID = "student-id"
    }
}