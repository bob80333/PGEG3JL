package com.erice.PGEG3JL

class Rom (val name: String, private val rom: ByteArray) {

    fun getByte(pointer: Int): Byte {
        return rom[pointer]
    }

    fun getBytes(pointer: Int, length: Int): ByteArray {
        return rom.sliceArray(IntRange(pointer, pointer + length -1))
    }

    fun setByte(pointer: Int, byte: Byte) {
        rom[pointer] = byte
    }

    fun setBytes(pointer: Int, bytes: ByteArray) {
        for (i in bytes.indices) {
            rom[pointer + i] = bytes[i]
        }
    }
}