package fr.toporin.satochip.viewmodel

import androidx.lifecycle.LiveData
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


class TransactionViewModel : ViewModel() {
    private val messageRepository: MessageRepository = MessageRepositoryImpl()
    private var factorItem: FactorItem? = null
    private val _generatedId2FA = MutableLiveData<String>()
    private val _secret2FA = MutableLiveData<String>()
    val generatedId2FA: LiveData<String> get() = _generatedId2FA
    val messageLiveData = MutableLiveData<String>()
    val authentiKeyLiveData = MutableLiveData<String>()
//    val msgJsonLiveData = MutableLiveData<Map<String, Any>>()
    val requestLiveData = MutableLiveData<String>()
    val rawRequestLiveData = MutableLiveData<String>()
    val reset2FALiveData = MutableLiveData<String>()



    fun initializeId2FA() {
        val secret2FA = "0b041c61d69bb0eacd3559dd8c894d7bbe46f618" // Utilise une variable locale pour stocker la valeur
        viewModelScope.launch(Dispatchers.IO) {
            _secret2FA.postValue(secret2FA) // Met à jour LiveData avec la valeur
            val secret2FAByteArray = secret2FA.toByteArray(Charsets.UTF_8) // Convertit la chaîne en ByteArray
            generateValues(secret2FAByteArray) // Appelle ta fonction avec le ByteArray
        }
    }

    fun generateValues(secret2FA: ByteArray) {
        factorItem = FactorItem(secret2FA)
        val id2FA = factorItem?.id2FA
        id2FA?.let {
            _generatedId2FA.postValue(it)
            getEncryptedMessage(it)
        }
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
                    decryptedMsg.containsKey("action") -> getReset2FA(decryptedMsg)
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

    }fun getReset2FA(decryptedMessage: Map<String, Any>) {
        val rawReset2FA = decryptedMessage["action"] as? String
        reset2FALiveData.postValue(rawReset2FA ?: "Reset 2FA non trouvé")
        println("reset2FA = $rawReset2FA")
    }
}
