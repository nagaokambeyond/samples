package com.example.demo.config

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.util.*
import java.util.Map
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenService(
    @param:Value("\${app.auth.jwt-secret}") private val secret: String,
    @param:Value("\${app.auth.expires-in-seconds}") val expiresInSeconds: Long
) {
    private val objectMapper: ObjectMapper
    private val clock: Clock

    init {
        this.objectMapper = ObjectMapper()
        this.clock = Clock.systemUTC()
    }

    fun createToken(authentication: Authentication): String {
        val now = clock.instant().getEpochSecond()
        val header = Map.of<String?, String?>(
            "alg", "HS256",
            "typ", "JWT"
        )
        val payload = Map.of<String?, Any?>(
            "sub", authentication.getName(),
            "iat", now,
            "exp", now + expiresInSeconds,
            "authorities", authentication.getAuthorities().stream()
                .map<String?> { authority: GrantedAuthority? -> authority!!.getAuthority() }
                .toList()
        )
        val headerAndPayload = encode(header) + "." + encode(payload)
        return headerAndPayload + "." + sign(headerAndPayload)
    }

    fun toAuthentication(token: String): Authentication {
        val parts: Array<String?> = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        require(parts.size == 3) { "Invalid token format" }

        val headerAndPayload = parts[0] + "." + parts[1]
        require(constantTimeEquals(sign(headerAndPayload)!!, parts[2]!!)) { "Invalid token signature" }

        val payload = decode(parts[1])
        val expiresAt = (payload.get("exp") as Number).toLong()
        require(expiresAt >= clock.instant().getEpochSecond()) { "Token expired" }

        val username = payload.get("sub") as String
        val authorities = (payload.get("authorities") as List<*>).stream()
            .map<SimpleGrantedAuthority> { authority: Any? -> SimpleGrantedAuthority(authority as String) }
            .toList()
        return UsernamePasswordAuthenticationToken.authenticated(username, null, authorities)
    }

    private fun encode(value: MutableMap<String?, *>?): String? {
        try {
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(value))
        } catch (ex: JsonProcessingException) {
            throw IllegalStateException("Failed to encode token", ex)
        }
    }

    private fun decode(value: String?): MutableMap<String?, Any?> {
        try {
            val json = Base64.getUrlDecoder().decode(value)
            return objectMapper.readValue<MutableMap<String?, Any?>>(json, MAP_TYPE)
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Failed to decode token", ex)
        } catch (ex: IOException) {
            throw IllegalArgumentException("Failed to decode token", ex)
        }
    }

    private fun sign(value: String): String? {
        try {
            val mac = Mac.getInstance(HMAC_SHA256)
            mac.init(SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), HMAC_SHA256))
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(mac.doFinal(value.toByteArray(StandardCharsets.UTF_8)))
        } catch (ex: Exception) {
            throw IllegalStateException("Failed to sign token", ex)
        }
    }

    private fun constantTimeEquals(expected: String, actual: String): Boolean {
        val expectedBytes = expected.toByteArray(StandardCharsets.UTF_8)
        val actualBytes = actual.toByteArray(StandardCharsets.UTF_8)
        if (expectedBytes.size != actualBytes.size) {
            return false
        }

        var result = 0
        for (i in expectedBytes.indices) {
            result = result or (expectedBytes[i].toInt() xor actualBytes[i].toInt())
        }
        return result == 0
    }

    companion object {
        private const val HMAC_SHA256 = "HmacSHA256"
        private val MAP_TYPE: TypeReference<MutableMap<String?, Any?>?> =
            object : TypeReference<MutableMap<String?, Any?>?>() {
            }
    }
}
