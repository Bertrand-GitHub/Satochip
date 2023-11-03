package fr.toporin.satochip.util

import java.util.UUID

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



