package fr.toporin.satochip.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import fr.toporin.satochip.viewmodel.MainViewModel
import fr.toporin.satochip.ui.theme.SatochipTheme
import fr.toporin.satochip.util.displayRequestInfo
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            val secret2FA = "0b041c61d69bb0eacd3559dd8c894d7bbe46f618"
            val secret2FAByteArray = secret2FA.toByteArray(charset = Charsets.UTF_8)
            viewModel.generateValues(secret2FAByteArray)
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
//    val id2FA = viewModel.id2FALiveData.observeAsState("").value
    val rawRequest = viewModel.rawRequestLiveData.observeAsState("").value
    val message = viewModel.messageLiveData.observeAsState("").value
    val authentiKey = viewModel.authentiKeyLiveData.observeAsState("").value
//    val msgJson = viewModel.msgJsonLiveData.observeAsState(mapOf()).value
    val action = viewModel.requestLiveData.observeAsState("").value

    println("action = $action")
    Column {
//        Text(text = "Id 2FA: $id2FA")
        Text(text = "Request: $action")
        Text(text = displayRequestInfo(rawRequest, message, authentiKey))
//        Text(text = "Message JSON: $msgJson")
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayValuesPreview(viewModel: MainViewModel?) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Column {
//            Text(text = "Id 2FA: 27024ce43b2e4e3e588b3e3e099879e0l7697869")
            Text(text = "Request: sign_tx")
            Text(text = "Message: Message de test")
//            Text(text = "Message JSON: Message de test JSON")

        }
    }

}
