package fr.toporin.satochip.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import fr.toporin.satochip.R
import fr.toporin.satochip.repository.QrCodeRepository
import fr.toporin.satochip.ui.component.CommonBottomNavigation
import fr.toporin.satochip.ui.component.CommonContainer
import fr.toporin.satochip.ui.component.CommonHeader
import fr.toporin.satochip.ui.component.ConfirmQrCodeButton
import fr.toporin.satochip.ui.component.ScanButton
import fr.toporin.satochip.ui.component.titleStyle
import fr.toporin.satochip.ui.theme.SatochipTheme
import fr.toporin.satochip.viewmodel.SharedViewModel
import fr.toporin.satochip.viewmodel.TransactionViewModel


class QrCodeFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
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
                    QrCodeScreen()
                }
            }
        }
    }

    @Composable
    fun QrCodeScreen() {
        val navController = findNavController()
        val showSnackbar by sharedViewModel.showSnackbar.observeAsState(initial = false)
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
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Text(
                            text = "First, scan the QR Code",
                            style = titleStyle,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        ScanButton(onClick = { navController.navigate(R.id.scanFragment) }
                        )

                        if (showSnackbar) {
                            Surface(color = Color(0xFFFFBB0B)) {
                                Snackbar(
                                    action = {
                                        TextButton(onClick = { sharedViewModel.showSnackbar.value = false }) {
                                            Text("Dismiss", color = Color.White)
                                        }
                                    },
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text("QR Code has been scanned.", color = Color.White)
                                }
                            }
                        }

                        Text(
                            text = "Second, add a label",
                            style = titleStyle,
                        )

                        TextField(
                            value = label2FA,
                            onValueChange = { label2FA = it },
                            label = { Text("example: BTC Wallet") }
                        )

                        ConfirmQrCodeButton {
                            transactionViewModel.initializeId2FA(qrCodeValue)
                            transactionViewModel.generatedId2FA.observe(viewLifecycleOwner) { id2FA ->
                                if (id2FA != null) {
                                    if (qrCodeRepository.getQrCodes().containsKey(id2FA)) {
                                        Toast.makeText(
                                            context,
                                            "ID2FA already stored",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        qrCodeRepository.saveQrCode(id2FA, label2FA)
                                        Toast.makeText(
                                            context,
                                            "ID2FA stored !",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
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
                CommonBottomNavigation(navController, currentScreenId = R.id.qrCodeFragment)
            }
        }
    }
}


