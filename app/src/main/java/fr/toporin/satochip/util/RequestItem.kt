package fr.toporin.satochip.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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


@Composable
fun DisplayRequestInfo(generatedId2FA: String, rawRequest: String, message: String, authentiKey: String) {
    when (UserFriendlyRequest.fromRawRequest(rawRequest)) {
        UserFriendlyRequest.RESET_SEED -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "AuthentiKey:",
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        color = Color(0xFFFFBB0B)))
                Text(
                    text = authentiKey,
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 24.sp,
                        color = Color(0xFFFFFFFF)))
            }
        }
        UserFriendlyRequest.SIGN_MESSAGE -> {
            Text(text = "Message: $message")
        }

        UserFriendlyRequest.RESET_2FA -> {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Your 2FA ID is:",
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        color = Color(0xFFFFBB0B)
                    )
                )
                Text(
                    text = generatedId2FA,
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 24.sp,
                        color = Color(0xFFFFFFFF)),
                    modifier = Modifier
                            .padding(horizontal = 24.dp)
                )
            }
        }
        else -> {
            Text(
                text = "Unknown request",
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 24.sp,
                    color = Color(0xFFFFFFFF)
                )
            )
        }
    }
}
