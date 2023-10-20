package fr.toporin.satochip.repository

interface MessageRepository {
    suspend fun fetchMessage(secret2FA: String): String?
}