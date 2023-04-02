package jp.co.arthurs.e2ee

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

object RsaUtils {
    private const val ALGORITHM = "RSA"
    private const val ENCRYPT_ALGORITHM = "RSA/ECB/OAEPPadding"
    private const val SHA_256 = "SHA-256"
    private const val MGF_NAME = "MGF1"
    private const val KEY_SIZE = 2048

    fun createKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance(ALGORITHM)
        generator.initialize(KEY_SIZE, SecureRandom())
        return generator.generateKeyPair()
    }

    fun generatePrivateKey(encodedKey: ByteArray): PrivateKey {
        return KeyFactory.getInstance(ALGORITHM).generatePrivate(PKCS8EncodedKeySpec(encodedKey))
    }

    fun decrypt(privateKey: PrivateKey, encrypted: ByteArray) : ByteArray {
        val oaepParams = OAEPParameterSpec(SHA_256, MGF_NAME, MGF1ParameterSpec(SHA_256), PSource.PSpecified.DEFAULT)
        val cipher = Cipher.getInstance(ENCRYPT_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams)
        return cipher.doFinal(encrypted)
    }

    fun encrypt(publicKeyBytes: ByteArray, bytes: ByteArray): ByteArray {
        val publicKey = KeyFactory.getInstance(ALGORITHM).generatePublic(X509EncodedKeySpec(publicKeyBytes))
        val oaepParams = OAEPParameterSpec(SHA_256, MGF_NAME, MGF1ParameterSpec(SHA_256), PSource.PSpecified.DEFAULT)
        val cipher = Cipher.getInstance(ENCRYPT_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams)
        return cipher.doFinal(bytes)
    }
}