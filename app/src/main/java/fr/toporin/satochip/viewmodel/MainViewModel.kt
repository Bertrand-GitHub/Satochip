package fr.toporin.satochip.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.toporin.satochip.model.enums.UserFriendlyRequest
import fr.toporin.satochip.repository.MessageRepository
import fr.toporin.satochip.repository.MessageRepositoryImpl
import fr.toporin.satochip.util.FactorItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel : ViewModel() {
    private val messageRepository: MessageRepository = MessageRepositoryImpl()
    private var factorItem: FactorItem? = null
//    val id2FALiveData = MutableLiveData<String>()
    val messageLiveData = MutableLiveData<String>()
    val authentiKeyLiveData = MutableLiveData<String>()
//    val msgJsonLiveData = MutableLiveData<Map<String, Any>>()
    val requestLiveData = MutableLiveData<String>()
    val rawRequestLiveData = MutableLiveData<String>()

    suspend fun generateValues(secret2FA: ByteArray) {
        factorItem = FactorItem(secret2FA)
        val generatedId2FA = factorItem!!.id2FA
//        id2FALiveData.postValue(generatedId2FA)
        getEncryptedMessage(generatedId2FA)
    }

    
    fun getEncryptedMessage(generatedId2FA: String) {
        viewModelScope.launch {
            println("getMessage appelé avec l'ID: $generatedId2FA")
            val message = withContext(Dispatchers.IO) { messageRepository.fetchMessage(generatedId2FA) }
            val decryptedMessage = message?.let { factorItem!!.decryptRequest(it) }

            println("msgJsonLiveData mis à jour = $decryptedMessage")

            decryptedMessage?.let { decryptedMsg ->
                getRequest(decryptedMsg)

                when {
                    decryptedMsg.containsKey("authentikeyx") -> getAuthentiKey(decryptedMsg)
                    decryptedMsg.containsKey("msg") -> getMessage(decryptedMsg)
                    else -> {
                        // Gérer le cas où aucune des clés n'est présente
                    }
                }
            }
        }
    }

    fun getRequest(decryptedMessage: Map<String, Any>) {
        val rawRequest = decryptedMessage["action"] as? String
        val userFriendlyRequest = UserFriendlyRequest.fromRawRequest(rawRequest ?: "")
        requestLiveData.postValue((userFriendlyRequest?.displayName ?: "Action non trouvée"))
        rawRequestLiveData.postValue(rawRequest ?: "Action non trouvée")
        println("actionLiveData mis à jour = $userFriendlyRequest")
    }

    //To Sign Message
    fun getMessage(decryptedMessage: Map<String, Any>) {
        val rawMessage = decryptedMessage["msg"] as? String
        messageLiveData.postValue(rawMessage ?: "Message non trouvé")
        println("messageLiveData mis à jour = $rawMessage")
    }

    // To Reset Seed
    fun getAuthentiKey(decryptedMessage: Map<String, Any>) {
        val rawAuthentiKey = decryptedMessage["authentikeyx"] as? String
        authentiKeyLiveData.postValue(rawAuthentiKey ?: "AuthentiKeyX non trouvé")
        println("authentiKey = $rawAuthentiKey")
    }
}
