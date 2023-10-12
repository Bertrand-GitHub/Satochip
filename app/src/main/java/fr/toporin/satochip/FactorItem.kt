import org.bouncycastle.crypto.digests.SHA1Digest
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.util.encoders.Hex
import org.bouncycastle.jce.provider.BouncyCastleProvider
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import org.json.JSONObject
import java.security.SecureRandom
import java.security.Security
import java.util.*

data class FactorItem(
    var id: UUID = UUID.randomUUID(),
    var secretBytes: ByteArray,
    var label: String = "",
    var id20Bytes: ByteArray,
    var id20Hex: String = "",
    var idBytes: ByteArray,
    var idHex: String = "",
    var idOtherBytes: ByteArray,
    var idOtherHex: String = "",
    var keyBytes: ByteArray
) {
    init {
        val hmacSha1 = HMac(SHA256Digest())
        hmacSha1.init(KeyParameter(secretBytes))

        // Hachage HMAC-SHA1 de l'identifiant 2FA et le place un tableau de bytes de 20 bytes
        id20Bytes = authenticate(hmacSha1, "id_2FA".toByteArray())
        // Conversion de id20Bytes en hexadécimal // "%02x" : 2 caractères hexadécimaux, avec un 0 en tête si nécessaire
        id20Hex = id20Bytes.joinToString("") { String.format("%02x", it) }
        println("id20Hex: $id20Hex")

        val digest = SHA256Digest() // Nouvelle instance de SHA256Digest
        idBytes = ByteArray(digest.digestSize) // Création d'un tableau de bytes de la taille du digest
        digest.update(id20Bytes, 0, id20Bytes.size)// Alimente le digest avec les 20 premiers bytes de l'identifiant 2FA
        digest.doFinal(idBytes, 0)  // Finalise le hachage et le stocke dans idBytes
        idHex = idBytes.joinToString("") { String.format("%02x", it) }
        println("idHex: $idHex")

        digest.reset() // Réinitialise le digest
        idOtherBytes = ByteArray(digest.digestSize) // Création d'un tableau de bytes de la taille du digest
        digest.update(idHex.toByteArray(), 0, idHex.toByteArray().size) // Alimente le digest avec les bytes de l'identifiant 2FA
        digest.doFinal(idOtherBytes, 0) //Finalise le hachage et le stocke dans idOtherBytes
        idOtherHex = idOtherBytes.joinToString("") { String.format("%02x", it) }
        println("idOtherHex: $idOtherHex")

        val keyBytes32 = authenticate(hmacSha1, "key_2FA".toByteArray()) // Hackage HMAC-SHA1 de la clé 2FA => tableau de bytes de 32 bytes
        keyBytes = keyBytes32.sliceArray(0 until 16) // Récupère les 16 premiers bytes de keyBytes32
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

    @Throws(Exception::class)
    fun decryptRequest(msgRaw: String): Map<String, Any> {

        val msgDecoded = Base64.getDecoder().decode(msgRaw) // Décodage Base64
        // IV (Initial Vector) = 16 premiers bytes du message
        val iv = msgDecoded.sliceArray(0 until 16) // Récupère les 16 premiers bytes
        val msgEncrypted = msgDecoded.sliceArray(16 until msgDecoded.size) // Récupère les bytes restants
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
        println("request msgJsonString: $msgJsonString")

        val msgJson = JSONObject(msgJsonString).toMap()
        println("request type of msgJson: ${msgJson::class.simpleName}")
        println("request msgJson: $msgJson")

        return msgJson
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
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC")
        val keySpec = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec) // Initialise le chiffrement avec la clé et l'IV
        val msgEncryptedBytes = cipher.doFinal(msgPlainBytes) // Chiffre le message
        val fullMessage = iv + msgEncryptedBytes // Concaténe IV et le message chiffré
        val msgEncryptedBase64 = Base64.getEncoder().encodeToString(fullMessage) // Convertir en Base64
        println("msgEncryptedBase64: $msgEncryptedBase64")

        return msgEncryptedBase64
    }

    //Fonctions utiles dans FactorItem
    //Fonction pour convertir un JSONObject en Map<String, Any>
    fun JSONObject.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keysItr: Iterator<String> = this.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            var value = this.get(key)

            if (value is JSONObject) {
                value = value.toMap()
            }
            map[key] = value
        }
        return map
    }

    private fun authenticate(hmac: HMac, message: ByteArray): ByteArray {
        val out = ByteArray(hmac.macSize)
        hmac.update(message, 0, message.size)
        hmac.doFinal(out, 0)
        return out
    }
}
