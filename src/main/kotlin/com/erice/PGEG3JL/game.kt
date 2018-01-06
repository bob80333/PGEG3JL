package com.erice.PGEG3JL

import java.io.InputStreamReader

enum class Game(val gameID: String) {
    Emerald("BPEE")
}

fun findGame(rom: Rom): Game {
    return Game.valueOf(rom.getBytes(0xAC, 4).toString())
}

class PokeTextDecoder(val game: Game) {
    val textDecode = mutableMapOf<Byte, String>()
    private val delimiter = " := "

    init {
        if (game == Game.Emerald) {
            loadEmeraldDecodeTable(textDecode)
        }
    }

    fun decodeText(bytes: ByteArray): String {
        var result = ""
        for (byte in  bytes) {
            result += textDecode[byte]
        }

        return result
    }

    private fun loadEmeraldDecodeTable(map: MutableMap<Byte, String>) {
        val reader = InputStreamReader(javaClass.classLoader.getResourceAsStream("BPEE_emerald_text_encoding.txt"))
        loadDecodeTable(reader, map)
    }

    private fun loadDecodeTable(reader: InputStreamReader, map: MutableMap<Byte, String>) {
        val mappings = reader.readLines()
        for (line in mappings) {
            val halves = line.split(delimiter)
            map[byteFromHex(halves[0].substring(2))] = halves[1]
        }
    }

}