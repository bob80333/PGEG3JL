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
        value = this[i].toPositiveInt() + (value * 256)
    }

    return value
}

fun ByteArray.toLong(): Long {
    var value = 0L
    if (this.size > 8) {
        println(this.size)
        throw IllegalArgumentException("Cannot convert a ByteArray with more than 8 bytes to an Long")
    }
    for (i in 0 until this.size) {
        if (i == 0) {
            value = this[i].toPositiveInt().toLong()
        }
        value = this[i].toPositiveInt() + (value * 256)
    }

    return value
}

fun ByteArray.toIntArray(): IntArray {
    val intArray = IntArray(this.size)
    this.forEachIndexed{ index, byte ->
        intArray[index] = byte.toPositiveInt()
    }

    return intArray
}

fun IntArray.toLong() : Long {
    if (this.size > 2) {
        throw IllegalArgumentException("Cannot convert an IntArray with more than 2 ints to an Long")
    }
    return (this[0].toLong() + (this[1].toLong() shl 8))
}
fun byteArrayFromHex(hex: String): ByteArray {
    val byteArray = ByteArray(hex.length / 2)
    for (i in 0 until hex.length step 2) {
        byteArray[i/2] = byteFromHex(hex[i].toString() + hex[i + 1].toString())
    }

    return byteArray
}

fun Byte.toPositiveInt() = toInt() and 0xFF

fun byteFromHex(hex: String) = hex.toInt(16).toByte()

fun Int.toROMPointer() = this and 0x1FFFFFF

fun Int.toGBAPointer() = this or 0x08000000

fun Int.toHexString() = "0x" + this.toString(16)
class IntLE(int: Int) {
    var value: Int = int
        private set(newVal) {
            field = switchEndianness(newVal)
        }
    get() {
        return switchEndianness(field)
    }

    fun getLE(): Int {
        return value
    }

    fun toROMPointer(): Int {
        return value.toROMPointer()
    }

    fun toGBAPointer(): Int {
        return value.toGBAPointer()
    }

    companion object {
        fun switchEndianness(value: Int): Int {
            return value and 0xff shl 24 or (value and 0xff00 shl 8) or (value and 0xff0000 shr 8) or (value shr 24 and 0xff)
        }
    }

}