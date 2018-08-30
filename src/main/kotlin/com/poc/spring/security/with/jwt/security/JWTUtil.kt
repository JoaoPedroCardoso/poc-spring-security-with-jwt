package com.poc.spring.security.with.jwt.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

/**
 * Created by JoaoPedroCardoso on 30/08/18
 */
@Component
class JWTUtil {

    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Value("\${jwt.expiration}")
    private val expiration: Long? = null

    fun generateToken(username: String): String {
        return Jwts.builder().setSubject(username).setExpiration(Date(System.currentTimeMillis() + expiration!!))
            .signWith(SignatureAlgorithm.HS512, secret!!.toByteArray()).compact()
    }

    fun tokenValido(token: String): Boolean {
        val claims = getClaims(token)
        if (claims != null) {
            val username = claims.subject
            val expirationDate = claims.expiration
            val now = Date(System.currentTimeMillis())
            if (username != null && expirationDate != null && now.before(expirationDate)) {
                return true
            }
        }
        return false
    }

    fun getUsername(token: String): String? {
        val claims = getClaims(token)
        return claims?.subject
    }

    private fun getClaims(token: String): Claims? {
        try {
            return Jwts.parser().setSigningKey(secret!!.toByteArray()).parseClaimsJws(token).body
        } catch (e: Exception) {
            return null
        }

    }
}
