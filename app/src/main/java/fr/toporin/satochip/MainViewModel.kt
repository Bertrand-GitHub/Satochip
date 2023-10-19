package fr.toporin.satochip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.timroes.axmlrpc.XMLRPCClient
import de.timroes.axmlrpc.XMLRPCException
import fr.toporin.satochip.util.FactorItem
import fr.toporin.satochip.util.RequestItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL


class MainViewModel : ViewModel() {
    private var factorItem: FactorItem? = null
    val id2FALiveData = MutableLiveData<String>()
    val messageLiveData = MutableLiveData<String>()
    val decryptedMessageLiveData = MutableLiveData<Map<String, Any>>()
    val msgJsonLiveData = MutableLiveData<Map<String, Any>>()

    fun generateValues(secret_2FA: ByteArray) {
        factorItem = FactorItem(secret_2FA)
        val generatedId2FA = factorItem!!.id2FA
        id2FALiveData.postValue(generatedId2FA)
        getMessage(generatedId2FA)
    }

    fun getId2FA(): String {
        return factorItem?.id2FA ?: ""
    }

    //val serverUrl = URL("https://cosigner.satochip.io")
    val serverUrl = URL("https://cosigner.electrum.org")

    suspend fun fetchMessage(id: String): String? {
        var message: String? = null
        try {
            val client = XMLRPCClient(serverUrl)
            val result = client.call("get", id)
            message = result.toString()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: XMLRPCException) {
            e.printStackTrace()
        }
        return message
    }

    fun getMessage(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            println("getMessage appelé avec l'ID: $id")

            val message = fetchMessage(id)
            println("Message récupéré: $message")

            messageLiveData.postValue(message ?: "Message vide")
            println("messageLiveData mis à jour.")

            val decryptedMap = factorItem!!.decryptRequest(message ?: "")
            decryptedMessageLiveData.postValue(decryptedMap)
            println("decryptedMessageLiveData mis à jour.")

            msgJsonLiveData.postValue(decryptedMap)
            println("msgJsonLiveData mis à jour.")
        }
    }
}
