package fr.toporin.satochip.model.enums

enum class UserFriendlyAction(val rawAction: String, val displayName: String) {
    RESET_SEED("reset_seed", "Reset seed"),
    RESET_2FA("reset_2FA", "Reset 2FA"),
    SIGN_MESSAGE("sign_msg", "Sign a message"),
    SIGN_TRANSACTION("sign_tx", "Sign a transaction"),
    SIGN_MESSAGE_HASH("sign_msg_hash", "Sign a hash message"),
    SIGN_TRANSACTION_HASH("sign_tx_hash", "Sign a hash transaction"),
    ;

    companion object {
        fun fromRawAction(rawAction: String): UserFriendlyAction? {
            return values().find { it.rawAction == rawAction }
        }
    }
}
