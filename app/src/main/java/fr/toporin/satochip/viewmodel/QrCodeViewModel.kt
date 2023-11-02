package fr.toporin.satochip.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QrCodeViewModel : ViewModel() {
    private val _scannedCode = MutableLiveData<String>()
    val scannedCode: LiveData<String> = _scannedCode

    fun setScannedCode(secret2FA: String) {
        _scannedCode.value = secret2FA
    }
}