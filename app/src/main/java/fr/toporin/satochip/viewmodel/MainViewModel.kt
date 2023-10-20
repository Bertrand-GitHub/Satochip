package fr.toporin.satochip.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.toporin.satochip.model.enums.UserFriendlyAction
import fr.toporin.satochip.repository.MessageRepository
import fr.toporin.satochip.repository.MessageRepositoryImpl
import fr.toporin.satochip.util.FactorItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private val messageRepository: MessageRepository = MessageRepositoryImpl()
    private var factorItem: FactorItem? = null
    val id2FALiveData = MutableLiveData<String>()
    val messageLiveData = MutableLiveData<String>()
    val msgJsonLiveData = MutableLiveData<Map<String, Any>>()
    val actionLiveData = MutableLiveData<String>()

    suspend fun generateValues(secret2FA: ByteArray) {
        factorItem = FactorItem(secret2FA)
        val generatedId2FA = factorItem!!.id2FA
        id2FALiveData.postValue(generatedId2FA)
        getMessage(generatedId2FA)
    }

    fun getMessage(generatedId2FA: String) {
        viewModelScope.launch(Dispatchers.IO) {
            println("getMessage appelé avec l'ID: $generatedId2FA")

            val message = messageRepository.fetchMessage(generatedId2FA)
            messageLiveData.postValue(message ?: "Message vide")

            val decryptedMessage = message?.let { factorItem!!.decryptRequest(it) }
            msgJsonLiveData.postValue(decryptedMessage)
            println("msgJsonLiveData mis à jour = $decryptedMessage")

            if (decryptedMessage != null) {
                getAction(decryptedMessage)
            }
        }
    }

    fun getAction(decryptedMessage: Map<String, Any>) {
        val rawAction = decryptedMessage["action"] as? String
        val userFriendlyAction = UserFriendlyAction.fromRawAction(rawAction ?: "")
        actionLiveData.postValue(userFriendlyAction?.displayName ?: "Action non trouvée")
        println("actionLiveData mis à jour = $userFriendlyAction")
    }
}
