package com.erice.PGEG3JL
val KB = 1_024
val MB = 1_048_576
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