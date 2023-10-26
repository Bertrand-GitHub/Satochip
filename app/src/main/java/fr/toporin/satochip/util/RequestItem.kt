package fr.toporin.satochip.util

import fr.toporin.satochip.model.enums.UserFriendlyRequest
import java.util.*

enum class WarningCode(val code: Int) {
    Ok(0),
    HashMismatch(1),
    UnsupportedSignMsgHashRequest(2),
    WrongMessageFormat(3),
    EIP712Unsupported(4),
    FailedToParseEIP712Msg(5)
}

interface RequestData {
    val type: String
    var challengeHex: String
    var warningCode: WarningCode
}

data class RequestItem(
    val id: UUID = UUID.randomUUID(),
    val idHex: String,
    val msgRaw: String,
    val label: String,
    var responseHex: String? = null,
    var requestData: RequestData? = null
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is RequestItem) {
            id == other.id
        } else {
            false
        }
    }
}

fun displayRequestInfo(rawRequest: String, message: String, authentiKey: String): String {
    val requestType = UserFriendlyRequest.fromRawRequest(rawRequest)
    return when (requestType) {
        UserFriendlyRequest.RESET_SEED -> "AuthentiKey: $authentiKey"
        UserFriendlyRequest.SIGN_MESSAGE -> "Message: $message"
        // Ajout de nouveaux types de requêtes ici
        else -> "Request inconnue"
    }
}