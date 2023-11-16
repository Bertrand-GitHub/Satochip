package fr.toporin.satochip.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import fr.toporin.satochip.R
import fr.toporin.satochip.ui.component.CommonBottomNavigation
import fr.toporin.satochip.ui.component.CommonContainer
import fr.toporin.satochip.ui.component.CommonHeader
import fr.toporin.satochip.ui.component.ConfirmQrCodeButton
import fr.toporin.satochip.ui.component.ScanButton
import fr.toporin.satochip.ui.theme.SatochipTheme
import fr.toporin.satochip.ui.theme.contentStyle
import fr.toporin.satochip.ui.theme.dataStyle
import fr.toporin.satochip.ui.theme.titleStyle
import fr.toporin.satochip.viewmodel.SharedViewModel


class QrCodeFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SatochipTheme {
                    QrCodeScreen(onCodeScanned = { /*TODO*/ })
                }
            }
        }
    }

    @Composable
    fun QrCodeScreen(onCodeScanned: (String) -> Unit) {
        val navController = findNavController()
        val qrCodeValue by sharedViewModel.qrCodeValue.observeAsState(initial = "")
        var label2FA by remember { mutableStateOf("") }


        Column(
            Modifier.fillMaxSize(),
        ) {
            // Title
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
            ) {
                CommonHeader(drawableId = R.drawable.title_qrcode)
            }
            // Container
            Box(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                CommonContainer {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(28.dp)
                    ) {
                        Text(
                            text = "First, scan the QR Code",
                            style = titleStyle,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        ScanButton(onClick = { navController.navigate(R.id.scanFragment) }
                        )

                        if (qrCodeValue.isNotEmpty()) {
                            Text(
                                text = "QR Code Value:",
                                style = dataStyle,
                            )

                            Text(
                                text = qrCodeValue,
                                style = contentStyle,
                                modifier = Modifier.padding(horizontal = 28.dp)
                            )
                        }
                        Text(
                            text = "Second, add a label",
                            style = titleStyle,
                        )

                        TextField(
                            value = label2FA,
                            onValueChange = { label2FA = it },
                            label = { Text("2FA label:") }
                        )

                        ConfirmQrCodeButton(onClick = { onCodeScanned(label2FA) })
                    }
                }
            }
            //Navigation
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth()
            ) {
                CommonBottomNavigation(navController, currentScreenId = R.id.qrCodeFragment)
            }
        }
    }
}


