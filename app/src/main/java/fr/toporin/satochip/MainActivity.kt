package fr.toporin.satochip

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import fr.toporin.satochip.ui.theme.SatochipTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            val secret_2FA = "0b041c61d69bb0eacd3559dd8c894d7bbe46f618".toByteArray(charset = Charsets.UTF_8)
            viewModel.generateValues(secret_2FA)
      }
        setContent {
            SatochipTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayValues(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun DisplayValues(viewModel: MainViewModel) {
    val id2FA = viewModel.id2FALiveData.observeAsState("").value
    val message = viewModel.messageLiveData.observeAsState("").value
    val decryptedMessageMap = viewModel.decryptedMessageLiveData.observeAsState(mapOf()).value
    val decryptedMessage = decryptedMessageMap[""] as? String ?: ""
    val msgJson = viewModel.msgJsonLiveData.observeAsState(mapOf()).value
    Column {
        Text(text = "Id 2FA: $id2FA")
        Text(text = "Message: $message")
        Text(text = "Decrypted Message: $decryptedMessage")
        Text(text = "Message JSON: $msgJson")
    }
}
