package fr.toporin.satochip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.timroes.axmlrpc.XMLRPCClient
import de.timroes.axmlrpc.XMLRPCClient.FLAGS_NIL
import de.timroes.axmlrpc.XMLRPCException
import de.timroes.axmlrpc.XMLRPCServerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.MalformedURLException
import java.net.URL

class MainViewModel : ViewModel() {

    val serverUrl = URL("https://cosigner.satochip.io")

    fun putMessage(id: String, msg: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val serverUrl = URL("https://cosigner.satochip.io")
            //Ajout du FLAGS_NIL pour que le serveur accepte les paramètres nil
            val client = XMLRPCClient(serverUrl, XMLRPCClient.FLAGS_NIL)
            val result = client.call("put", id, msg)

            if (result == null) {
                println("Message envoyé et réponse du serveur est nil, considéré comme succès.")
            } else {
                println("Message envoyé, réponse du serveur : $result")
            }
        } catch (ex: XMLRPCServerException) {
            // Le serveur a renvoyé une erreur.
            println("Erreur du serveur : ${ex.message}")
        } catch (ex: MalformedURLException) {
            // L'URL du serveur est invalide.
            println("Erreur serveur URL invalide : ${ex.message}")
        } catch (ex: XMLRPCException) {
            // Une autre exception.
            println("Erreur : ${ex.message}")
        }
    }

    suspend fun getMessage(id: String): String? = withContext(Dispatchers.IO) {
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
        message
    }
}