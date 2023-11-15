package fr.toporin.satochip.util

import fr.toporin.satochip.model.interfaces.RequestData
import java.util.UUID


data class RequestItem(
    val id: UUID = UUID.randomUUID(),
    val idHex: String,
    val msgRaw: String,
    val label: String,
    var responseHex: String? = null,
    var requestData: RequestData? = null
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is RequestItem) {
            id == other.id
        } else {
            false
        }
    }
}



