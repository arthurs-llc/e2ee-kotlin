package jp.co.arthurs.e2ee

import jakarta.validation.constraints.NotEmpty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.set
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.nio.charset.StandardCharsets
import java.util.*

@RestController
@RequestMapping("/e2ee")
class E2eeAuthController(val redisCacheManager: RedisCacheManager) {
    companion object {
        const val PRIVATE_KEY_CACHE = "privateKeyCache"
    }

    private val logger: Logger = LoggerFactory.getLogger(E2eeAuthController::class.java)

    @GetMapping("/public-key")
    fun getPublicKey(): KeyIdAndPublicKey {
        val keyPair = RsaUtils.createKeyPair()

        val keyId = UUID.randomUUID().toString()
        val privateKey = Base64.getEncoder().encodeToString(keyPair.private.encoded)
        redisCacheManager.getCache(PRIVATE_KEY_CACHE)!![keyId] = privateKey
        return KeyIdAndPublicKey(keyId, Base64.getUrlEncoder().encodeToString(keyPair.public.encoded))
    }

    @PostMapping("/login")
    fun postSecureData(@RequestBody @Validated credential: E2eeCredential): ResponseEntity<Unit> {
        val base64PrivateKey = redisCacheManager.getCache(PRIVATE_KEY_CACHE)!!.get(credential.keyId)
        if (base64PrivateKey?.get() == null) {
            return ResponseEntity.badRequest().build()
        }
        redisCacheManager.getCache(PRIVATE_KEY_CACHE)!!.evict(credential.keyId)

        return try {
            val encryptedPassword = Base64.getUrlDecoder().decode(credential.password)
            val privateKey = RsaUtils.generatePrivateKey(Base64.getDecoder().decode(base64PrivateKey.get() as String))
            val plainPassword = String(RsaUtils.decrypt(privateKey, encryptedPassword), StandardCharsets.UTF_8)
            logger.info("plainPassword: $plainPassword")
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            logger.error("Failed to decrypt password.", e)
            ResponseEntity.badRequest().build()
        }
    }
}

data class KeyIdAndPublicKey(val keyId: String, val publicKey: String)

data class E2eeCredential(@NotEmpty val email: String, @NotEmpty val password: String, @NotEmpty val keyId: String)