package fr.toporin.satochip.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _qrCodeValue = MutableLiveData<String>()
    val qrCodeValue: LiveData<String> get() = _qrCodeValue

    fun setQrCodeValue(value: String) {
        _qrCodeValue.value = value
    }
}