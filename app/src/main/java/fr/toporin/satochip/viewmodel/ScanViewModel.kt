package fr.toporin.satochip.viewmodel

import android.graphics.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.barcode.common.Barcode

class ScanViewModel : ViewModel() {
    val qrContent = MutableLiveData<String>()
    var boundingRect: Rect? = null

    fun processBarcode(barcode: Barcode) {
        boundingRect = barcode.boundingBox

        when (barcode.valueType) {
            Barcode.TYPE_TEXT -> {
                qrContent.value = barcode.rawValue
            }
            else -> {
                qrContent.value = "Unsupported data type: ${barcode.rawValue}"
            }
        }
    }
}
