package fr.toporin.satochip.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import fr.toporin.satochip.ui.theme.SatochipTheme
import fr.toporin.satochip.util.QrCodeDrawable
import fr.toporin.satochip.viewmodel.ScanViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.navigation.fragment.findNavController
import fr.toporin.satochip.viewmodel.SharedViewModel


class ScanFragment : Fragment() {
    private var isScanning = true
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var cameraController: LifecycleCameraController
    private lateinit var previewView: PreviewView

    // Initialisation du ActivityResultLauncher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(context, "Permission not granted by the user.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SatochipTheme {
                    CameraPreview()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraController = LifecycleCameraController(requireContext())
        previewView = PreviewView(requireContext()).apply {
            controller = cameraController
        }
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(requireContext()),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(requireContext())
            ) { result: MlKitAnalyzer.Result? ->
                if (!isScanning) return@MlKitAnalyzer

                val barcodeResults = result?.getValue(barcodeScanner)
                if (barcodeResults.isNullOrEmpty()) {
                    previewView.overlay.clear()
                    return@MlKitAnalyzer
                }

                isScanning = false
                val qrCodeValue = barcodeResults.first().rawValue
                sharedViewModel.setQrCodeValue(qrCodeValue ?: "")

                val qrCodeViewModel = ScanViewModel()
                qrCodeViewModel.processBarcode(barcodeResults[0])
                val qrCodeDrawable = QrCodeDrawable(qrCodeViewModel)

                previewView.overlay.clear()
                previewView.overlay.add(qrCodeDrawable)

                val action = qrCodeValue?.let {
                    ScanFragmentDirections.actionScanFragmentToQrCodeFragment()
                }
                if (action != null) {
                    sharedViewModel.showSnackbar.value = true
                    findNavController().navigate(action)
                }

            }
        )
        cameraController.bindToLifecycle(viewLifecycleOwner)
        previewView.controller = cameraController
    }
    private fun stopCamera() {
        barcodeScanner.close()
        cameraExecutor.shutdown()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopCamera()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    @Composable
    fun CameraPreview() {
        AndroidView(factory = { previewView })
    }
}


