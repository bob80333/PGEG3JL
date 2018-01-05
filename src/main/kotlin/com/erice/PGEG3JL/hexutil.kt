package com.erice.PGEG3JL

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

fun Byte.toPositiveInt() = toInt() and 0xFF

fun byteFromHex(hex: String) = hex.toInt(16).toByte()