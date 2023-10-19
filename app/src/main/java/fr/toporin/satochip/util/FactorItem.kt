package fr.toporin.satochip.util

import org.bouncycastle.crypto.digests.SHA1Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.Security
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

data class FactorItem(private val secret_2FA: ByteArray) {
    val id2FA: String
    var keyBytes: ByteArray
    init {
        //HMAC SHA1
        val message = "id_2FA".toByteArray(Charsets.UTF_8)
        val hmac = Mac.getInstance("HmacSHA1")
        val keySpec = SecretKeySpec(Hex.decode(secret_2FA), "HmacSHA1")
        hmac.init(keySpec)
        val hmacBytes = hmac.doFinal(message)
        //SHA256
        val hmac2FAHex = Hex.decode(String(Hex.encode(hmacBytes)))
        val sha256Digest = MessageDigest.getInstance("SHA-256")
        val id2FABytes = sha256Digest.digest(hmac2FAHex)
        id2FA = String(Hex.encode(id2FABytes))
        println("id_2FA: $id2FA")
        // Calcule idOtherBytes et idOtherHex
        val idHexBytes = id2FA.toByteArray(Charsets.UTF_8)
        val sha256DigestForIdOther = MessageDigest.getInstance("SHA-256")
        val idOtherBytes = sha256DigestForIdOther.digest(idHexBytes)
        val idOtherHex = String(Hex.encode(idOtherBytes)).uppercase()
        println("Received response from: $idOtherHex")
        // Calcule keyBytes
        val messageForKey = "key_2FA".toByteArray(Charsets.UTF_8)
        val hmacForKey = Mac.getInstance("HmacSHA1")
        val keySpecForKey = SecretKeySpec(Hex.decode(secret_2FA), "HmacSHA1")
        hmacForKey.init(keySpecForKey)
        val hmacBytesForKey = hmacForKey.doFinal(messageForKey)
        // Garde les 16 premiers octets sur 20
        keyBytes = hmacBytesForKey.sliceArray(0 until 16)
    }

    @Throws(Exception::class)
    fun decryptRequest(msgRaw: String): Map<String, Any> {
        val msgDecoded = Base64.getDecoder().decode(msgRaw) // Décodage Base64
        // IV (Initial Vector) = 16 premiers bytes du message
        val iv = msgDecoded.sliceArray(0 until 16)
        val msgEncrypted = msgDecoded.sliceArray(16 until msgDecoded.size)
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", BouncyCastleProvider())
        val keySpec = SecretKeySpec(keyBytes, "AES")  // Création de la clé AES
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val msgDecrypted = cipher.doFinal(msgEncrypted)
        // Décoder le JSON
        val msgJsonString = try {
            String(msgDecrypted, Charsets.UTF_8)
            } catch (e: Exception) {
                throw IllegalArgumentException("Decryption failed, msgDecrypted cannot be converted to String", e)
        }
        val jsonObject = JSONObject(msgJsonString)
        val msgJson = jsonObject.keys().asSequence().associateWith {
            jsonObject.get(it)
        } as Map<String, Any>
        return msgJson
    }

    fun approveChallenge(challengeHex: String, secretBytes: ByteArray): String {
        val challengeBytes = Hex.decode(challengeHex)
        val hmac = HMac(SHA1Digest())
        val keyParams = KeyParameter(secretBytes)
        hmac.init(keyParams) // Initialise le HMac avec la clé secrète
        val responseBytes = authenticate(hmac, challengeBytes)
        val responseHex = String(Hex.encode(responseBytes))
        return responseHex
    }

    fun rejectChallenge(challengeHex: String): String {
        return "00".repeat(20)
    }

    fun encryptRequest(msgPlainTxt: String, keyBytes: ByteArray): String {
        Security.addProvider(BouncyCastleProvider())

        // Génére IV (Initial Vector) de manière aléatoire
        val iv = ByteArray(16)
        try {
            val random = SecureRandom()
            random.nextBytes(iv)
        } catch (e: Exception) {
            // Générer IV de manière moins sécurisée si la première méthode échoue
            for (i in 0 until 16) {
                iv[i] = (0..255).random().toByte()
            }
        }

        // Converti le texte brut en tableau de bytes
        val msgPlainBytes = msgPlainTxt.toByteArray()
        // Initialise AES avec le mode CBC et PKCS7Padding
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        val keySpec = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec) // Initialise le chiffrement avec la clé et l'IV
        val msgEncryptedBytes = cipher.doFinal(msgPlainBytes) // Chiffre le message
        val fullMessage = iv + msgEncryptedBytes // Concaténe IV et le message chiffré
        val msgEncryptedBase64 = Base64.getEncoder().encodeToString(fullMessage) // Convertir en Base64
        println("msgEncryptedBase64: $msgEncryptedBase64")
        return msgEncryptedBase64
    }

    //Fonctions utiles dans fr.toporin.satochip.util.FactorItem
    private fun authenticate(hmac: HMac, message: ByteArray): ByteArray {
        val out = ByteArray(hmac.macSize)
        hmac.reset()
        hmac.update(message, 0, message.size)
        hmac.doFinal(out, 0)
        return out
    }
}
