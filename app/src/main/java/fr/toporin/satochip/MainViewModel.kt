package fr.toporin.satochip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.timroes.axmlrpc.XMLRPCClient
import de.timroes.axmlrpc.XMLRPCException
import fr.toporin.satochip.repository.MessageRepository
import fr.toporin.satochip.repository.MessageRepositoryImpl
import fr.toporin.satochip.util.FactorItem
import fr.toporin.satochip.util.RequestItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL


class MainViewModel : ViewModel() {
    private val messageRepository: MessageRepository = MessageRepositoryImpl()
    private var factorItem: FactorItem? = null
    val id2FALiveData = MutableLiveData<String>()
    val messageLiveData = MutableLiveData<String>()
    val msgJsonLiveData = MutableLiveData<Map<String, Any>>()

    suspend fun generateValues(secret_2FA: ByteArray) {
        factorItem = FactorItem(secret_2FA)
        val generatedId2FA = factorItem!!.id2FA
        id2FALiveData.postValue(generatedId2FA)
        getMessage(generatedId2FA)
    }

    fun getMessage(generatedId2FA: String) {
        viewModelScope.launch(Dispatchers.IO) {
            println("getMessage appelé avec l'ID: $generatedId2FA")

            val message = messageRepository.fetchMessage(generatedId2FA)
            messageLiveData.postValue(message ?: "Message vide")

            val decryptedMessage = factorItem!!.decryptRequest(message ?: "")
            msgJsonLiveData.postValue(decryptedMessage)
            println("msgJsonLiveData mis à jour = $decryptedMessage")
        }
    }
}
