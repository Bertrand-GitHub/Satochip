package fr.toporin.satochip.model.interfaces

import fr.toporin.satochip.model.enums.WarningCode

interface RequestData {
    val type: String
    var challengeHex: String
    var warningCode: WarningCode
}