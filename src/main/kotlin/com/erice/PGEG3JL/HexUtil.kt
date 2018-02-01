package com.erice.PGEG3JL
const val KB = 1_024
const val MB = 1_048_576

const val FULL_POINTER_BYTES = 4
const val SMALL_POINTER_BYTES = 3
const val INT_BYTES = 4
const val SHORT_BYTES = 2
fun ByteArray.toAsciiString(): String {
    var asciiString = ""
    for (byte in this) {
        asciiString += byte.toChar()
    }
    return asciiString
}

fun ByteArray.toHexString(): String {
    var hexString = "0x"
    for (byte in this) {
        hexString += byte.toString(16)
    }
    return hexString
}

fun ByteArray.toInt(): Int {
    var value = 0
    if (this.size > 4) {
        throw IllegalArgumentException("Cannot convert a ByteArray with more than 4 bytes to an Int")
    }
    for (i in 0 until this.size) {
        if (i == 0) {
            value = this[i].toPositiveInt()
        }
        value += this[i].toPositiveInt() * 256 * i
    }

    return value
}

fun Byte.toPositiveInt() = toInt() and 0xFF

fun byteFromHex(hex: String) = hex.toInt(16).toByte()

class IntLE(int: Int) {
    var value: Int = int
        private set(newVal) {
            field = switchEndianness(newVal)
        }
        get() {
            return toBE()
        }

    private fun toBE(): Int {
        return switchEndianness(value)
    }

    fun getLE(): Int {
        return value
    }

    companion object {
        fun switchEndianness(value: Int): Int {
            return value and 0xff shl 24 or (value and 0xff00 shl 8) or (value and 0xff0000 shr 8) or (value shr 24 and 0xff)
        }
    }

}