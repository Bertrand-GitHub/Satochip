package fr.toporin.satochip.util

import java.security.MessageDigest

enum class TxState {
    TX_START,
    TX_PARSE_INPUT,
    TX_PARSE_INPUT_SCRIPT,
    TX_PARSE_OUTPUT,
    TX_PARSE_OUTPUT_SCRIPT,
    TX_PARSE_FINALIZE,
    TX_END
}

class TxParser(var txBytes: ByteArray) {

    var txOffset = 0
    var txRemaining: Int = txBytes.size
    var isParsed = false

    // shared
    var txVersion = ByteArray(0)
    var outScripts = mutableListOf<ByteArray>()
    var outAmounts = mutableListOf<Long>()
    var singleHash = ByteArray(0)
    var doubleHash = ByteArray(0)

    // legacy tx
    var txOutHashArray = mutableListOf<ByteArray>()
    var txOutIndexArray = mutableListOf<Int>()
    var inputScriptArray = mutableListOf<ByteArray>()

    // segwit tx
    var hashPrevouts = ByteArray(0)
    var hashSequence = ByteArray(0)
    var txOutHash = ByteArray(0)
    var txOutIndex: Int = 0
    var inputAmount: Long = 0
    var inputScript = ByteArray(0)
    var nSequence = ByteArray(0)
    var hashOutputs = ByteArray(0)
    var nLocktime = ByteArray(0)
    var nHashType = ByteArray(0)


    fun parseOutputs() {
        var txRemainingOutputs = parseVarInt().toInt()

        while (txRemainingOutputs > 0) {
            val amountBytes = parseBytes(length = 8) // amount
            outAmounts.add(readUInt64(bytes = amountBytes, offset = 0))

            val txRemainingScripts = parseVarInt().toInt()
            val scripts = parseBytes(length = txRemainingScripts)
            outScripts.add(scripts)
            txRemainingOutputs--
        }

        // Update hash
        singleHash = sha256(txBytes)
        doubleHash = sha256(singleHash)
        isParsed = true
    }


    fun parseSegwitTransaction() {
        // TxState.TX_START
        txVersion = parseBytes(4)
        hashPrevouts = parseBytes(32)
        hashSequence = parseBytes(32)
        // parse outpoint
        txOutHash = parseBytes(32)
        txOutHash = reverseBytes(txOutHash)
        val txOutIndexBytes = parseBytes(4)
        txOutIndex = readUInt32(txOutIndexBytes, 0)
        // scriptcode= varint+script
        val txRemainingScripts = parseVarInt().toInt()
        println("Debug txRemainingScripts: $txRemainingScripts")
        // TxState.TX_PARSE_INPUT_SCRIPT
        inputScript = parseBytes(txRemainingScripts)

        // TxState.TX_PARSE_FINALIZE:
        val inputAmountBytes = parseBytes(8)
        println("inputAmountBytes: ${inputAmountBytes.toHex()}")
        inputAmount = readUInt64(inputAmountBytes, 0)
        println("inputAmount: $inputAmount")
        nSequence = parseBytes(4)
        hashOutputs = parseBytes(32)
        nLocktime = parseBytes(4)
        nHashType = parseBytes(4)

        //TxState.TX_END
        // update hash
        singleHash = sha256(txBytes)
        doubleHash = sha256(singleHash)
        isParsed = true
    }


    fun ByteArray.toHex(): String = joinToString(separator = "") { byte -> "%02x".format(byte) }


    private fun parseBytes(length: Int): ByteArray {
        // Vérifier la longueur et les limites
        if (length <= 0 || txOffset + length > txBytes.size) {
            throw IllegalArgumentException("Invalid length or offset")
        }
        val chunk = txBytes.copyOfRange(txOffset, txOffset + length)
        txOffset += length
        txRemaining -= length
        return chunk
    }

    fun parseVarInt(): Long {
        val first = txBytes[txOffset].toInt() and 0xFF
        val value: Long
        val length: Int

        when {
            first < 253 -> {
                // 8 bits
                value = first.toLong()
                length = 1
            }
            first == 253 -> {
                // 16 bits
                value = (txBytes[txOffset + 1].toInt() and 0xFF).toLong() or
                        ((txBytes[txOffset + 2].toInt() and 0xFF).toLong() shl 8)
                length = 3
            }
            first == 254 -> {
                // 32 bits
                value = readUInt32(txBytes, txOffset + 1).toLong()
                length = 5
            }
            else -> {
                // 64 bits
                value = readUInt64(txBytes, txOffset + 1)
                length = 9
            }
        }

        txOffset += length
        txRemaining -= length
        return value
    }

    fun readUInt32(bytes: ByteArray, offset: Int): Int {
        return (bytes[offset].toInt() and 0xFF) or
                ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
                ((bytes[offset + 2].toInt() and 0xFF) shl 16) or
                ((bytes[offset + 3].toInt() and 0xFF) shl 24)
    }

    fun readUInt64(bytes: ByteArray, offset: Int): Long {
        return (bytes[offset].toLong() and 0xFF) or
                ((bytes[offset + 1].toLong() and 0xFF) shl 8) or
                ((bytes[offset + 2].toLong() and 0xFF) shl 16) or
                ((bytes[offset + 3].toLong() and 0xFF) shl 24) or
                ((bytes[offset + 4].toLong() and 0xFF) shl 32) or
                ((bytes[offset + 5].toLong() and 0xFF) shl 40) or
                ((bytes[offset + 6].toLong() and 0xFF) shl 48) or
                ((bytes[offset + 7].toLong() and 0xFF) shl 56)
    }

    fun outputScriptToH160(scriptHex: String): String {
        var scriptHexOut = scriptHex
        scriptHexOut = if (scriptHex.startsWith("76")) {
            scriptHexOut.drop(6)
        } else {
            scriptHexOut.drop(4)
        }

        scriptHexOut = if (scriptHex.endsWith("88AC")) {
            scriptHexOut.dropLast(4)
        } else {
            scriptHexOut.dropLast(2)
        }

        println("Satochip: scriptHex:    $scriptHex")
        println("Satochip: scriptHexOut: $scriptHexOut")
        return scriptHexOut
    }

    fun reverseBytes(inBytes: ByteArray): ByteArray {
        return inBytes.reversedArray()
    }

    fun sha256(input: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(input)
    }

    companion object {
        fun sha256(input: ByteArray): ByteArray {
            val md = MessageDigest.getInstance("SHA-256")
            return md.digest(input)
        }

        fun reverseBytes(inBytes: ByteArray): ByteArray {
            return inBytes.reversedArray()
        }
    }
}
