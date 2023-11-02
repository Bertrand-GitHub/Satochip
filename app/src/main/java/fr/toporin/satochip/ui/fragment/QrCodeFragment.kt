package fr.toporin.satochip.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import fr.toporin.satochip.R
import fr.toporin.satochip.ui.theme.SatochipTheme

class QrCodeFragment : Fragment() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                //TODO
                // Permission is granted. Continue the action or workflow in your app.
            } else {
                //TODO
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their decision.
            }
        }
    }
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

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    fun scanBarcodes(image: InputImage) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val secret2FAScan = barcode.rawValue
                    // Utilise `rawValue` comme la valeur du QR Code scannée
                }
            }
            .addOnFailureListener {
                // Gère l'échec de la lecture du QR Code
            }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun QrCodeScreen(onCodeScanned: (String) -> Unit) {
        val navController = findNavController()
        var label2FA by remember { mutableStateOf("") }
        Column(
            Modifier.fillMaxSize(),
        ) {
            // Title
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .background(color = Color(0xFF1A1B2B)),
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .size(width = 300.dp, height = 300.dp),
                    painter = painterResource(id = R.drawable.title_transaction),
                    contentDescription = ""
                )
            }
            // Container
            Box(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxSize()
                    .background(color = Color(0xFF1A1B2B)),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(
                            color = Color(0xFF2A2B3B),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Button(onClick = { requestCameraPermission() }) {
                            Text("Scanner QR Code")
                        }

                        TextField(
                            value = label2FA,
                            onValueChange = { label2FA = it },
                            label = { Text("2FA label:") }
                        )
                        Button(onClick = {
                            onCodeScanned(label2FA)
                        }) {
                            Text("Confirm QrCode")
                        }
                    }
                }
            }
            //Navigation
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .background(color = Color(0xFF1A1B2B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clickable { navController.navigate(R.id.action_qrCodeFragment_to_transactionFragment) },
                        painter = painterResource(id = R.drawable.ic_transaction_default),
                        contentDescription = ""
                    )
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp),
                        painter = painterResource(id = R.drawable.ic_qrcode_active),
                        contentDescription = ""
                    )
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clickable { navController.navigate("settings") },
                        painter = painterResource(id = R.drawable.ic_settings_default),
                        contentDescription = ""
                    )
                    Image(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clickable { navController.navigate("logs") },
                        painter = painterResource(id = R.drawable.ic_logs_default),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}



