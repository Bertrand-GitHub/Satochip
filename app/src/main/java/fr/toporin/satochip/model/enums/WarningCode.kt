package fr.toporin.satochip.model.enums

enum class WarningCode(val code: Int) {
    Ok(0),
    HashMismatch(1),
    UnsupportedSignMsgHashRequest(2),
    WrongMessageFormat(3),
    EIP712Unsupported(4),
    FailedToParseEIP712Msg(5)
}