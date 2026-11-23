package org.ironman.data

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCryptoManager {

    private val MAGIC_BYTES = byteArrayOf('I'.code.toByte(), 'W'.code.toByte(), 'D'.code.toByte(), 'B'.code.toByte())
    private const val VERSION: Byte = 1

    private const val SALT_SIZE_BYTES = 16
    private const val IV_SIZE_BYTES = 12
    private const val TAG_SIZE_BITS = 128
    private const val PBKDF2_ITERATIONS = 10000
    private const val KEY_SIZE_BITS = 256
    private const val DEFAULT_PASSPHRASE = "IRON_WILL_SECURE_SQLITE_BACKUP_DEFAULT_KEY"

    fun isEncryptedBackup(bytes: ByteArray): Boolean {
        if (bytes.size < MAGIC_BYTES.size) return false
        for (i in MAGIC_BYTES.indices) {
            if (bytes[i] != MAGIC_BYTES[i]) return false
        }
        return true
    }

    fun encryptDatabase(dbBytes: ByteArray, pin: String): ByteArray {
        val passphrase = if (pin.isNotBlank()) pin else DEFAULT_PASSPHRASE
        val random = SecureRandom()

        val salt = ByteArray(SALT_SIZE_BYTES)
        random.nextBytes(salt)

        val iv = ByteArray(IV_SIZE_BYTES)
        random.nextBytes(iv)

        val secretKey = deriveKey(passphrase, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(TAG_SIZE_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val encryptedPayload = cipher.doFinal(dbBytes)

        val headerSize = MAGIC_BYTES.size + 1 + SALT_SIZE_BYTES + IV_SIZE_BYTES
        val result = ByteArray(headerSize + encryptedPayload.size)

        var offset = 0
        System.arraycopy(MAGIC_BYTES, 0, result, offset, MAGIC_BYTES.size)
        offset += MAGIC_BYTES.size

        result[offset] = VERSION
        offset += 1

        System.arraycopy(salt, 0, result, offset, SALT_SIZE_BYTES)
        offset += SALT_SIZE_BYTES

        System.arraycopy(iv, 0, result, offset, IV_SIZE_BYTES)
        offset += IV_SIZE_BYTES

        System.arraycopy(encryptedPayload, 0, result, offset, encryptedPayload.size)

        return result
    }

    fun decryptDatabase(encryptedBytes: ByteArray, pin: String): ByteArray? {
        if (!isEncryptedBackup(encryptedBytes)) return null

        val headerSize = MAGIC_BYTES.size + 1 + SALT_SIZE_BYTES + IV_SIZE_BYTES
        if (encryptedBytes.size < headerSize) return null

        val passphrase = if (pin.isNotBlank()) pin else DEFAULT_PASSPHRASE

        var offset = MAGIC_BYTES.size
        val version = encryptedBytes[offset]
        offset += 1

        if (version != VERSION) return null

        val salt = ByteArray(SALT_SIZE_BYTES)
        System.arraycopy(encryptedBytes, offset, salt, 0, SALT_SIZE_BYTES)
        offset += SALT_SIZE_BYTES

        val iv = ByteArray(IV_SIZE_BYTES)
        System.arraycopy(encryptedBytes, offset, iv, 0, IV_SIZE_BYTES)
        offset += IV_SIZE_BYTES

        val encryptedPayloadSize = encryptedBytes.size - offset
        val encryptedPayload = ByteArray(encryptedPayloadSize)
        System.arraycopy(encryptedBytes, offset, encryptedPayload, 0, encryptedPayloadSize)

        return try {
            val secretKey = deriveKey(passphrase, salt)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(TAG_SIZE_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

            cipher.doFinal(encryptedPayload)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun deriveKey(passphrase: String, salt: ByteArray): SecretKey {
        val keySpec = PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_SIZE_BITS)
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = keyFactory.generateSecret(keySpec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }
}
