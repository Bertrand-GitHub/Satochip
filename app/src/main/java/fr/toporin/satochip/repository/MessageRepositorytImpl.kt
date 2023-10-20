package fr.toporin.satochip.repository

import de.timroes.axmlrpc.XMLRPCClient
import de.timroes.axmlrpc.XMLRPCException
import java.net.MalformedURLException
import java.net.URL

class MessageRepositoryImpl : MessageRepository {
    private val serverUrl = URL("https://cosigner.electrum.org")
    override suspend fun fetchMessage(secret2FA: String): String? {
        var message: String? = null
        try {
            val client = XMLRPCClient(serverUrl)
            val result = client.call("get", secret2FA)
            message = result.toString()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: XMLRPCException) {
            e.printStackTrace()
        }
        return message
    }
}