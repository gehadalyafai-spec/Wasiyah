package com.example.data.crypto

import com.example.data.model.DebtItem
import com.example.data.model.SpecialRightItem
import com.example.data.model.TrustDepositItem
import com.example.data.model.WasiyyahDocument
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * محرك التشفير والأمان المتقدم لخزنة "وَصِيَّة"
 * يوفر:
 * 1. تشفير متقدم AES-256-GCM (Zero-Knowledge) للبيانات الحساسة.
 * 2. اشتقاق مفاتيح قوي عبر PBKDF2WithHmacSHA256 مع ملح عشوائي (Salt).
 * 3. توليد وتدقيق الختم الرقمي والتجزئة الرياضية SHA-256.
 * 4. تطبيق خوارزمية تقسيم المفاتيح التوافقية Shamir's Secret Sharing (2 of 3).
 */
object CryptoVaultManager {

    private const val AES_GCM_TAG_LENGTH_BITS = 128
    private const val IV_LENGTH_BYTES = 12
    private const val SALT_LENGTH_BYTES = 16
    private const val PBKDF2_ITERATIONS = 10000
    private const val KEY_LENGTH_BITS = 256

    private val secureRandom = SecureRandom()

    // Prime number for Shamir's Secret Sharing field arithmetic (GF(P))
    private const val SHAMIR_PRIME: Long = 2147483647L // 2^31 - 1 Mersenne prime

    const val CIPHER_ALGORITHM_NAME = "AES-256-GCM"
    const val KEY_DERIVATION_NAME = "PBKDF2-HMAC-SHA256"
    const val HASH_ALGORITHM_NAME = "SHA-256 Cryptographic Seal"
    const val SHAMIR_SCHEME_NAME = "Shamir's SSS (Threshold: 2 of 3)"

    /**
     * تشفير نص باستخدام خوارزمية AES-256-GCM المشتقة من رمز المرور
     * الناتج بصيغة مشفرة محمية: [SaltHex]:[IvHex]:[CipherHex]
     */
    fun encrypt(plainText: String, pin: String): String {
        if (plainText.isEmpty()) return ""

        val salt = ByteArray(SALT_LENGTH_BYTES).also { secureRandom.nextBytes(it) }
        val iv = ByteArray(IV_LENGTH_BYTES).also { secureRandom.nextBytes(it) }

        val keySpec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(keySpec).encoded
        val secretKey = SecretKeySpec(keyBytes, "AES")

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(AES_GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val cipherBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

        return "${bytesToHex(salt)}:${bytesToHex(iv)}:${bytesToHex(cipherBytes)}"
    }

    /**
     * فك تشفير نص مشفر بـ AES-256-GCM
     * يتحقق تلقائياً من صحة رمز المرور عبر التحقق من سلامة المصادقة (AEAD GCM Authentication Tag)
     */
    fun decrypt(encryptedPayload: String, pin: String): Result<String> {
        return runCatching {
            val parts = encryptedPayload.split(":")
            if (parts.size != 3) {
                // Return as is if not matching encrypted format
                return@runCatching encryptedPayload
            }

            val salt = hexToBytes(parts[0])
            val iv = hexToBytes(parts[1])
            val cipherBytes = hexToBytes(parts[2])

            val keySpec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val keyBytes = factory.generateSecret(keySpec).encoded
            val secretKey = SecretKeySpec(keyBytes, "AES")

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(AES_GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, StandardCharsets.UTF_8)
        }
    }

    /**
     * حساب بصمة الهاش الرقمية المعتمدة للوثيقة الشرعية SHA-256
     */
    fun calculateDocumentHash(
        doc: WasiyyahDocument,
        debts: List<DebtItem> = emptyList(),
        trusts: List<TrustDepositItem> = emptyList(),
        rights: List<SpecialRightItem> = emptyList()
    ): String {
        val canonical = buildString {
            append("TESTATOR:").append(doc.testatorName).append(";")
            append("ID:").append(doc.testatorNationalId).append(";")
            append("WEALTH:").append(doc.totalEstimatedWealth).append(";")
            append("THIRD_AMT:").append(doc.thirdBequestAmount).append(";")
            append("BENEFICIARY:").append(doc.thirdBequestBeneficiary).append(";")
            append("OPENING:").append(doc.testimonyOpening).append(";")
            append("DIRECTIVES:").append(doc.spiritualDirectives).append(";")
            append("SIGNATURE:").append(doc.electronicSignature).append(";")
            append("W1:").append(doc.witness1Name).append(":").append(doc.witness1NationalId).append(";")
            append("W2:").append(doc.witness2Name).append(":").append(doc.witness2NationalId).append(";")
            append("DEBTS_COUNT:").append(debts.size).append(";")
            append("TRUSTS_COUNT:").append(trusts.size).append(";")
            append("RIGHTS_COUNT:").append(rights.size).append(";")
            append("VERSION:").append(doc.version).append(";")
        }
        return computeSha256(canonical)
    }

    /**
     * حساب تجزئة SHA-256 لأي نص أو معطى
     */
    fun computeSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
        return "SHA-256: ${bytesToHex(hash)}"
    }

    /**
     * توليد حصص الأوصياء بناءً على خوارزمية Shamir's Secret Sharing (2 of 3)
     * المعادلة: f(x) = (a * x + Secret) mod P
     */
    fun generateShamirShares(
        secretValue: Long,
        totalShares: Int = 3
    ): List<Pair<Int, Long>> {
        val safeSecret = (secretValue % (SHAMIR_PRIME - 1) + (SHAMIR_PRIME - 1)) % (SHAMIR_PRIME - 1) + 1
        // Random slope coefficient 'a'
        val a = (secureRandom.nextInt(100000) + 1L) % SHAMIR_PRIME

        val shares = mutableListOf<Pair<Int, Long>>()
        for (x in 1..totalShares) {
            val fx = ((a * x) + safeSecret) % SHAMIR_PRIME
            shares.add(Pair(x, fx))
        }
        return shares
    }

    /**
     * إعادة بناء السر المشترك من أي حصتين (Lagrange Interpolation at x=0)
     */
    fun reconstructSecret(share1: Pair<Int, Long>, share2: Pair<Int, Long>): Long {
        val (x1, y1) = share1
        val (x2, y2) = share2

        // Lagrange interpolation at x = 0:
        // L1(0) = -x2 / (x1 - x2)
        // L2(0) = -x1 / (x2 - x1)
        // S = y1 * L1(0) + y2 * L2(0) (mod P)
        val numerator1 = ((-x2.toLong() % SHAMIR_PRIME) + SHAMIR_PRIME) % SHAMIR_PRIME
        val denominator1 = (((x1 - x2).toLong() % SHAMIR_PRIME) + SHAMIR_PRIME) % SHAMIR_PRIME
        val inv1 = modInverse(denominator1, SHAMIR_PRIME)
        val term1 = (y1 % SHAMIR_PRIME * numerator1 % SHAMIR_PRIME * inv1) % SHAMIR_PRIME

        val numerator2 = ((-x1.toLong() % SHAMIR_PRIME) + SHAMIR_PRIME) % SHAMIR_PRIME
        val denominator2 = (((x2 - x1).toLong() % SHAMIR_PRIME) + SHAMIR_PRIME) % SHAMIR_PRIME
        val inv2 = modInverse(denominator2, SHAMIR_PRIME)
        val term2 = (y2 % SHAMIR_PRIME * numerator2 % SHAMIR_PRIME * inv2) % SHAMIR_PRIME

        return (term1 + term2) % SHAMIR_PRIME
    }

    private fun modInverse(n: Long, m: Long): Long {
        var a = n % m
        for (x in 1L until m) {
            if ((a * x) % m == 1L) return x
        }
        return 1L
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = "0123456789abcdef"
        val result = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            val i = b.toInt() and 0xFF
            result.append(hexChars[i ushr 4])
            result.append(hexChars[i and 0x0F])
        }
        return result.toString()
    }

    private fun hexToBytes(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }
}
