package fr.toporin.satochip.util


class SegwitAddr {

    private val CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"
    private val CHARSET_INVERSE = CHARSET.withIndex().associate { it.value to it.index }

    val BECH32_CONST = 1
    val BECH32M_CONST = 0x2bc830a3


    enum class Encoding {
        BECH32,
        BECH32M
    }

    data class DecodedBech32(
        val encoding: Encoding?,
        val hrp: String?,
        val data: List<Int>?
    )

    private fun bech32Polymod(values: List<Int>): Int {
        val generator = listOf(0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3)
        var chk = 1
        values.forEach { value ->
            val top = chk shr 25
            chk = (chk and 0x1ffffff shl 5) xor value
            for (i in 0 until 5) {
                chk = chk xor (if ((top shr i) and 1 == 1) generator[i] else 0)
            }
        }
        return chk
    }

    private fun bech32HrpExpand(hrp: String): List<Int> {
        return hrp.map { it.code shr 5 } + 0 + hrp.map { it.code and 31 }
    }

    private fun bech32VerifyChecksum(hrp: String, data: List<Int>): Encoding? {
        val check = bech32Polymod(bech32HrpExpand(hrp) + data)
        return when (check) {
            BECH32_CONST -> Encoding.BECH32
            BECH32M_CONST -> Encoding.BECH32M
            else -> null
        }
    }

    private fun bech32CreateChecksum(encoding: Encoding, hrp: String, data: List<Int>): List<Int> {
        val values = bech32HrpExpand(hrp) + data
        val const = if (encoding == Encoding.BECH32M) BECH32M_CONST else BECH32_CONST
        val polymod = bech32Polymod(values + listOf(0, 0, 0, 0, 0, 0)) xor const
        return (0 until 6).map { (polymod shr 5 * (5 - it)) and 31 }
    }

    private fun bech32Encode(encoding: Encoding, hrp: String, data: List<Int>): String {
        val combined = data + bech32CreateChecksum(encoding, hrp, data)
        return hrp + "1" + combined.joinToString("") { CHARSET[it].toString() }
    }

    private fun bech32Decode(bech: String, ignoreLongLength: Boolean = false): DecodedBech32 {
        val bechLower = bech.lowercase()
        if (bechLower != bech && bech.uppercase() != bech) {
            return DecodedBech32(null, null, null)
        }
        val pos = bech.lastIndexOf('1')
        if (pos < 1 || pos + 7 > bech.length || (!ignoreLongLength && bech.length > 90)) {
            return DecodedBech32(null, null, null)
        }
        if (bech.substring(0, pos + 1).any { it.code < 33 || it.code > 126 }) {
            return DecodedBech32(null, null, null)
        }
        val hrp = bechLower.substring(0, pos)
        val data = try {
            bechLower.substring(pos + 1).map { CHARSET_INVERSE[it] ?: throw IllegalArgumentException() }
        } catch (e: IllegalArgumentException) {
            return DecodedBech32(null, null, null)
        }
        val encoding = bech32VerifyChecksum(hrp, data)
        return if (encoding == null) DecodedBech32(null, null, null) else DecodedBech32(encoding, hrp, data.dropLast(6))
    }

    fun convertBits(data: List<Int>, fromBits: Int, toBits: Int, pad: Boolean = true): List<Int> {
        var acc = 0
        var bits = 0
        val ret = mutableListOf<Int>()
        val maxv = (1 shl toBits) - 1
        val maxAcc = (1 shl (fromBits + toBits - 1)) - 1

        for (value in data) {
            if (value < 0 || (value shr fromBits) != 0) {
                return emptyList() // ou gérer l'erreur différemment
            }
            acc = ((acc shl fromBits) or value) and maxAcc
            bits += fromBits
            while (bits >= toBits) {
                bits -= toBits
                ret.add((acc shr bits) and maxv)
            }
        }

        if (pad) {
            if (bits > 0) {
                ret.add((acc shl (toBits - bits)) and maxv)
            }
        } else if (bits >= fromBits || ((acc shl (toBits - bits)) and maxv) != 0) {
            return emptyList()
        }

        return ret
    }

    private fun decodeSegwitAddress(hrp: String, addr: String?): Pair<Int?, List<Int>?> {
        if (addr == null) {
            return Pair(null, null)
        }
        val (encoding, hrpgot, data) = bech32Decode(addr)
        if (hrpgot != hrp) {
            return Pair(null, null)
        }
        val decoded = data?.let { convertBits(it.drop(1), 5, 8, false) }
        if (decoded == null || decoded.size < 2 || decoded.size > 40) {
            return Pair(null, null)
        }
        if (data[0] > 16) {
            return Pair(null, null)
        }
        if (data[0] == 0 && decoded.size != 20 && decoded.size != 32) {
            return Pair(null, null)
        }
        if ((data[0] == 0 && encoding != Encoding.BECH32) || (data[0] != 0 && encoding != Encoding.BECH32M)) {
            return Pair(null, null)
        }
        return Pair(data[0], decoded)
    }

    fun encodeSegwitAddress(hrp: String, witver: Int, witprog: ByteArray): String? {
        val encoding = if (witver == 0) Encoding.BECH32 else Encoding.BECH32M
        val witprogAsIntList = witprog.map { it.toInt() and 0xff } // Convertit Byte en Int
        val ret = bech32Encode(encoding, hrp, listOf(witver) + convertBits(witprogAsIntList, 8, 5))
        if (decodeSegwitAddress(hrp, ret) == Pair(null, null)) {
            return null
        }
        return ret
    }

}