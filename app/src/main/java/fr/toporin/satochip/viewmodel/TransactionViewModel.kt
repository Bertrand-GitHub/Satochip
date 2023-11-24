package fr.toporin.satochip.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.toporin.satochip.model.enums.UserFriendlyRequest
import fr.toporin.satochip.repository.MessageRepository
import fr.toporin.satochip.repository.MessageRepositoryImpl
import fr.toporin.satochip.util.FactorItem
import fr.toporin.satochip.util.TxParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.satochip.javacryptotools.Bitcoin

import java.security.MessageDigest

class TransactionViewModel : ViewModel() {
    private val messageRepository: MessageRepository = MessageRepositoryImpl()
    private var factorItem: FactorItem? = null
    private val _generatedId2FA = MutableLiveData<String>()
    private val _secret2FA = MutableLiveData<String>()
    private lateinit var coinObject: Bitcoin
    private var segwitHrp: String = ""

    val generatedId2FA: LiveData<String> get() = _generatedId2FA
    val messageLiveData = MutableLiveData<String>()
    val authentiKeyLiveData = MutableLiveData<String>()
    val requestLiveData = MutableLiveData<String>()
    val rawRequestLiveData = MutableLiveData<String?>()
    val reset2FALiveData = MutableLiveData<String>()
    val signTxLiveData = MutableLiveData<String>()
//    val msgJsonLiveData = MutableLiveData<Map<String, Any>>()


    fun initializeId2FA(qrCodeValue: String) {
        val secret2FA = qrCodeValue.toByteArray(Charsets.UTF_8)
        viewModelScope.launch(Dispatchers.IO) {
            _secret2FA.postValue(qrCodeValue)
            println("qrCodeValue = $qrCodeValue")
            generateValues(secret2FA)
            println("secret2FABytes = $secret2FA")
        }
    }

    private fun generateValues(secret2FA: ByteArray) {
        factorItem = FactorItem(secret2FA)
        val id2FA = factorItem?.id2FA
        id2FA?.let {
            _generatedId2FA.postValue(it)
            getEncryptedMessage(it)
        }
    }

    private fun getEncryptedMessage(generatedId2FA: String) {
        viewModelScope.launch {
            println("getMessage appelé avec l'ID: $generatedId2FA")
            val message = withContext(Dispatchers.IO) { messageRepository.fetchMessage(generatedId2FA) }
            val decryptedRequest = message?.let { factorItem!!.decryptRequest(it) }

            println("msgJsonLiveData mis à jour = $decryptedRequest")

            decryptedRequest?.let { decryptedMsg ->
                val rawRequest = getRequest(decryptedMsg)
                println("get Encrypted Message rawRequest = $rawRequest")

                when (rawRequest) {
                    "reset_seed" -> getResetSeedRequest(decryptedMsg)
//                    "sign_msg" -> getSignMessageRequest(decryptedMsg)
                    "reset_2FA" -> getReset2FARequest(decryptedMsg)
                    "tx" -> getSignTxRequest(decryptedMsg)
                    else -> {
                        // Gérer le cas où aucune des clés n'est présente
                    }
                }
            }
        }
    }

    fun getRequest(decryptedMessage: Map<String, Any>): String {
        val action = decryptedMessage["action"] as? String
        val tx = decryptedMessage["tx"] as? String
        val rawRequest = when {
            action != null -> action
            tx != null -> "tx"
            else -> "No Request Found!"
        }

        val userFriendlyRequest = UserFriendlyRequest.fromRawRequest(rawRequest)
        val displayName = userFriendlyRequest?.displayName ?: "Action inconnue"
        requestLiveData.postValue(displayName)
        rawRequestLiveData.postValue(rawRequest)
        println("actionLiveData mis à jour = $displayName")
        println("et le rawrequest associé est = $rawRequest")

        return rawRequest
    }

    //To Sign Message
//    fun getSignMessageRequest(decryptedMessage: Map<String, Any>) {
//        val rawMessage = decryptedMessage["msg"] as? String
//        val msgHashHex: String
//        var challengeHex = String
//        var responseHex = "00".repeat(20)  // reject by default
//        val altcoin = decryptedMessage["alt"] as? String ?: "Bitcoin"
//        val headerSize = byteArrayOf((altcoin.toByteArray().size + 17).toByte())
//        val msgBytes = rawMessage?.toByteArray(Charsets.UTF_8)
//        var msgPaddedBytes = headerSize + altcoin.toByteArray(Charsets.UTF_8) + " Signed Message:\n".toByteArray(Charsets.UTF_8)
//        if (msgBytes != null) {
//            msgPaddedBytes += VarInt(msgBytes.size).data + msgBytes
//        }
//
//        msgHashHex = TxParser.sha256(msgPaddedBytes).toHex()
//        challengeHex = msgHashHex + "BB".repeat(32)
//
//
//
//        messageLiveData.postValue(rawMessage ?: "Message non trouvé")
//        println("messageLiveData mis à jour = $rawMessage")
//    }

    // To Reset Seed
    fun getResetSeedRequest(decryptedMessage: Map<String, Any>) {
        val rawAuthentiKey = decryptedMessage["authentikeyx"] as? String
        authentiKeyLiveData.postValue(rawAuthentiKey ?: "AuthentiKeyX non trouvé")
        println("authentiKey = $rawAuthentiKey")
    }

    fun getReset2FARequest(decryptedMessage: Map<String, Any>) {
        val rawReset2FA = decryptedMessage["action"] as? String
        reset2FALiveData.postValue(rawReset2FA ?: "Reset 2FA non trouvé")
        println("reset2FA = $rawReset2FA")
    }

    private fun getSignTxRequest(decryptedMessage: Map<String, Any>) {
        val coinObject = Bitcoin(true, emptyMap())
        val challengeHex: String
        val inputs = mutableListOf<String>()
        val outputs = mutableListOf<String>()
        val inputAmounts = mutableListOf<Double?>()
        val outputAmounts = mutableListOf<Double?>()
        var fee: Double? = null

        val txHex = decryptedMessage["tx"] as? String
        val coinType = decryptedMessage["ct"] as? Int
        val isSegwit = decryptedMessage["sw"] as? Boolean
        val txoHex = decryptedMessage["txo"] as? String
        val txInType = decryptedMessage["ty"] as? String

        when (coinType) {
            0 -> this.coinObject = Bitcoin(false, emptyMap())
            1 -> this.coinObject = Bitcoin(true, emptyMap())
//            2 -> coinObject = Litecoin(isTestnet = isTestnet, apiKeys = emptyMap())
//            145 -> coinObject = BitcoinCash(isTestnet = isTestnet, apiKeys = emptyMap())
            else -> {
                println("Satochip: Coin not (yet) supported: $coinType")
            }
        }

        var txParsed: String = "Coin: " + coinObject.display_name + "\n"
        println(txParsed)
        val txBytes = txHex?.hexToBytes()
        val txHashBytes = txBytes?.sha256()?.sha256()
        val txHashHex = txHashBytes?.toHex()
        challengeHex = txHashHex + "00".repeat(32)

        if (isSegwit == true) {
            // Parse segwit tx
            println("txinType: $txInType")
            val txparser = txBytes?.let { TxParser(it) }
            if (txparser != null) {
                txparser.parseSegwitTransaction()
                println("Satochip: hashPrevouts: ${txparser.hashPrevouts.toHex().uppercase()}")
                println("Satochip: hashSequence: ${txparser.hashSequence.toHex()}")
                println("Satochip: txOutHash: ${txparser.txOutHash.toHex()}")
                println("Satochip: txOutIndex: ${txparser.txOutIndex}")
                println("Satochip: inputScript: ${txparser.inputScript.toHex()}")
                println("Satochip: inputAmount: ${txparser.inputAmount}")
                println("Satochip: nSequence: ${txparser.nSequence.toHex()}")
                println("Satochip: hashOutputs: ${txparser.hashOutputs.toHex()}")
                println("Satochip: nLocktime: ${txparser.nLocktime.toHex()}")
                println("Satochip: nHashType: ${txparser.nHashType.toHex()}")

            }
            // ... et ainsi de suite pour les autres propriétés

            val scriptHex = txparser?.inputScript?.toHex()?.uppercase()
            println("Satochip: inputScript: $scriptHex")

            val addr: String? = when (txInType) {
                "p2wpkh" -> {
                    val txParserInstance =
                        txBytes?.let { TxParser(it) }
                    val hashHex = scriptHex?.let { txParserInstance?.outputScriptToH160(it) }
                    val hashBytes = hashHex?.hexToBytes()
                    println("Satochip: hashBytes: $hashBytes")

                    val address = coinObject.pubToSegwitAddress(hashBytes)
                    println("Satochip: p2wpkh address: $address")
                    address
                }
                // ... et ainsi de suite pour les autres types
                else -> "script non supporté : $scriptHex \n"
            }
            println("addr: $addr")
            if (addr != null) {
                inputs.add(addr)
            }

            if (txparser != null) {
                inputAmounts.add(txparser.inputAmount.toDouble())
            }
            txParsed += "input:\n"
            txParsed += "\t address: $addr spent: ${(txparser?.inputAmount)?.div(100000)} \n"  // satoshi to mBtc

// parse outputs
            val txoBytes = txoHex?.hexToBytes()
            val txoParser = txoBytes?.let { TxParser(txBytes = it) }
            txoParser?.parseOutputs()
            val hashOutputsBytes = txoBytes?.drop(1)?.let { TxParser.sha256(it.toByteArray()) }?.let { TxParser.sha256(it) }
            println("Satochip: hashOutputsBytes: ${hashOutputsBytes?.toHex()}")
            val nbOuts = txoParser?.outAmounts?.size
            println("nbOuts= $nbOuts")

            txParsed += "outputs ($nbOuts):\n"

            var amountOut: Long = 0
            for (i in 0 until nbOuts!!) {
                val amount = txoParser.outAmounts[i]
                amountOut += amount
                val scriptBytes = txoParser.outScripts[i]
                val localScriptHex = scriptBytes.toHex().uppercase()
                var isDataScript = false
                println("Satochip: outScripts: ${scriptBytes.toHex().uppercase()}")
                println("Satochip: amount: $amount")

                var addrOut: String
                when {
                    localScriptHex.startsWith("76A914") -> { // p2pkh
                        addrOut = coinObject.pubToAddress(scriptBytes)
                    }
                    localScriptHex.startsWith("A914") -> { // p2sh
                        addrOut = coinObject.pubToAddress(scriptBytes)
                    }
                    localScriptHex.startsWith("0014") -> { // p2wpkh
                        val hashBytes = scriptBytes.drop(2).toByteArray()
                        addrOut = coinObject.pubToSegwitAddress(hashBytes)
                        println("DEBUG p2wpkh: $addrOut")
                    }
                    localScriptHex.startsWith("0020") -> { // p2wsh
                        val hashBytes = scriptBytes.drop(2).toByteArray()
                        addrOut = coinObject.pubToSegwitAddress(hashBytes)
                    }
                    localScriptHex.startsWith("6A") -> { // op_return data script
                        addrOut = if (localScriptHex.startsWith("6A04534C5000")) {
                            // SLP token
                            "TODO: SLP parsing"
                        } else {
                            val dataBytes = scriptBytes.drop(3)
                            val dataStr = String(dataBytes.toByteArray(), Charsets.UTF_8)
                            "DATA: $dataStr"
                        }
                        isDataScript = true
                    }
                    else -> {
                        addrOut = "unsupported script: $localScriptHex \n"
                    }
                }

                if (coinType == 145 && !isDataScript) {
                    addrOut = "TODO: convert addr $addrOut to cashaddress"
                }
                println("Satochip: output address: $addrOut")
                txParsed += "\t address: $addrOut spent: ${amountOut.toDouble() / 100000} \n" // satoshi to mBtc
                outputs.add(addrOut)
                outputAmounts.add(amountOut.toDouble())
            }

        }

        signTxLiveData.postValue(txHex ?: "Tx non trouvé")
        println("tx = $txHex")
        println("ct = $coinType")
        println("sw = $isSegwit")
        println("txo = $txoHex")
        println("ty = $txInType")
    }

    //Fonctions nécessaire pour getSignTxRequest
    fun String.hexToBytes(): ByteArray {
        val result = ByteArray(length / 2)
        for (i in 0 until length step 2) {
            val byte = substring(i, i + 2).toInt(16).toByte()
            result[i / 2] = byte
        }
        return result
    }

    fun ByteArray.sha256(): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(this)
    }

    fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }

}
