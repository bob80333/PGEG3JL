package com.erice.PGEG3JL

class Rom (val name: String, val rom: ByteArray) {

    fun getByte(pointer: Int): Byte {
        return rom[pointer]
    }

    fun getPointer(pointer: Int, fullPointer: Boolean = false): Int {
        val bytes = rom.sliceArray(IntRange(pointer, pointer + 3))
        if (!fullPointer) {
            bytes[3] = 0
        }
        //GBA is little endian, but we want to index big endian
        bytes.reverse()
        return bytes.toInt()
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