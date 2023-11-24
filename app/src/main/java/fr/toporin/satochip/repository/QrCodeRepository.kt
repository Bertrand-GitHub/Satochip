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


    fun saveQrCode(qrCode: String, id2FA: String, label: String) {
        val qrCodeData = "$qrCode,$id2FA,$label"
        sharedPreferences.edit {
            putString(id2FA, qrCodeData)
            commit()
        }
    }

    fun getQrCodesData(): Map<String, Triple<String, String, String>> {
        val allEntries = sharedPreferences.all
        return allEntries.mapValues { entry ->
            val dataParts = entry.value.toString().split(",")
            if (dataParts.size == 3) {
                Triple(dataParts[0], dataParts[1], dataParts[2])
            } else {
                Triple("", "", "")
            }
        }
    }

    fun deleteQrCode(id2FA: String) {
        sharedPreferences.edit {
            remove(id2FA)
            commit()
        }
    }
}