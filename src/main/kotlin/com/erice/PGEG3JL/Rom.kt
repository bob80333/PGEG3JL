package com.erice.PGEG3JL

/**
 * Holds the rom's data (typically 16mb) and provides utility methods to access its data.
 */
class Rom (val name: String, val rom: ByteArray) {

    /**
     * @param pointer: An Int pointing to the rom location where the byte is to be retrieved.
     * @return The byte at the pointer
     */
    fun getByte(pointer: Int): Byte {
        return rom[pointer]
    }

    /**
     * @param pointer: the location to get the pointer from
     * @param fullPointer: should we use the last (first) byte? (0x80) that is used in the GBA to reference the ROM?
     * @return the pointer as an Int
     */
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