package fr.toporin.satochip.repository

import android.content.Context
import androidx.core.content.edit
import java.net.URL

class QrCodeRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("qr_codes", Context.MODE_PRIVATE)
    private var serverUrl = URL("https://cosigner.electrum.org")
    fun changeServer(server: String) {
        serverUrl = URL(server)
    }


    fun saveQrCode(qrCode: String, label: String) {
        sharedPreferences.edit {
            putString(qrCode, label)
        }
    }

    fun getQrCodes(): Map<String, String> {
        return sharedPreferences.all.filterValues { it is String }.mapValues { it.value as String }
    }

    fun deleteQrCode(qrCode: String) {
        sharedPreferences.edit {
            remove(qrCode)
        }
    }
}