package fr.toporin.satochip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import fr.toporin.satochip.MainViewModel
import fr.toporin.satochip.ui.theme.SatochipTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SatochipTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // autres composants UI
                }
            }
        }

        viewModel.putMessage("561534ec392fa8eebf5779b233232f7f7df5fd5179c3c640d84378ee6274686b", "Je suis une Licorne ! 🦄")
        viewModel.putMessage("661534ec392fa8eebf5779b233232f7f7df5fd5179c3c640d84378ee6274686b", "Et pourquoi pas ? ^^")

        viewModel.viewModelScope.launch {
            val message2 = viewModel.getMessage("561534ec392fa8eebf5779b233232f7f7df5fd5179c3c640d84378ee6274686b")
            println("Message reçu: $message2")
        }


        viewModel.viewModelScope.launch {
            val message1 = viewModel.getMessage("661534ec392fa8eebf5779b233232f7f7df5fd5179c3c640d84378ee6274686b")
            println("Message reçu: $message1")
        }


    }
}
