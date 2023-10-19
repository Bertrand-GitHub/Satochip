package fr.toporin.satochip.repository

interface MessageRepository {
    suspend fun fetchMessage(id: String): String?
}