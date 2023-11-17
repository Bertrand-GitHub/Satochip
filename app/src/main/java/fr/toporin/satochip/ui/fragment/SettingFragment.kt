package fr.toporin.satochip.ui.fragment

import fr.toporin.satochip.repository.QrCodeRepository
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fr.toporin.satochip.R
import fr.toporin.satochip.ui.component.CommonBottomNavigation
import fr.toporin.satochip.ui.component.CommonContainer
import fr.toporin.satochip.ui.component.CommonHeader
import fr.toporin.satochip.ui.theme.SatochipTheme
import fr.toporin.satochip.ui.component.textStyle
import fr.toporin.satochip.ui.component.title2Style
import fr.toporin.satochip.ui.component.titleStyle

class SettingFragment : Fragment() {

    private lateinit var qrCodeRepository: QrCodeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qrCodeRepository = QrCodeRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SatochipTheme {
                    SettingsScreen()
                }
            }
        }
    }


    @Composable
    fun SettingsScreen() {
        val navController = findNavController()
        val qrCodes = qrCodeRepository.getQrCodes()
        var expanded by remember { mutableStateOf(false) }
        var selectedServer by remember { mutableStateOf("https://cosigner.electrum.org") }
        val servers = listOf("https://cosigner.electrum.org", "https://cosigner.satochip.io")

        Column(
            Modifier.fillMaxSize(),
        ) {
            // Title
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
            ) {
                CommonHeader(drawableId = R.drawable.title_settings)
            }
            // Container
            Box(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxSize()
                    .background(color = Color(0xFF1A1B2B)),
                contentAlignment = Alignment.TopCenter,
            ) {
                CommonContainer {
                    Column {
                        Box (
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "List of stored 2FA:",
                                style = titleStyle,
                                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                            )
                        }

                        qrCodes.forEach { (qrCode, label) ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = "2FA label:",
                                    style = title2Style,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp),
                                )
                                Text(
                                    text = label,
                                    style = textStyle,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                Text(
                                    text = "2FA value:",
                                    style = title2Style,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth()
                                )
                                Text(
                                    text = qrCode,
                                    style = textStyle,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = {
                                        qrCodeRepository.deleteQrCode(qrCode)
                                        Toast.makeText(context, "ID2FA removed", Toast.LENGTH_SHORT)
                                            .show()
                                        requireActivity().recreate()
                                    }) {
                                    Text(text = "REMOVE")
                                }
                            }
                        }
                    }
                }
            }

            //Navigation
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
            ) {
                CommonBottomNavigation(navController, currentScreenId = R.id.settingsFragment)
            }
        }
    }
}




