package com.erice.PGEG3JL

import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader

enum class Game(val gameID: String) {
    Emerald("BPEE")
}

fun findGame(rom: Rom): Game {
    return Game.valueOf(rom.getBytes(0xAC, 4).toString())
}

class PokeTextDecoder(val game: Game) {
    val textDecode = mutableMapOf<Byte, String>()
    private val delimeter = " := "

    init {
        if (game == Game.Emerald) {
            loadEmeraldDecodeTable(textDecode)
        }
    }

    public fun decodeText(bytes: ByteArray): String {
        var result = ""
        for (byte in  bytes) {
            result += textDecode[byte]
        }

        return result
    }

    private fun loadEmeraldDecodeTable(map: MutableMap<Byte, String>) {
        val reader = InputStreamReader(javaClass.classLoader.getResourceAsStream("emerald_text_encoding.txt"))
        loadDecodeTable(reader, map)
    }

    private fun loadDecodeTable(reader: InputStreamReader, map: MutableMap<Byte, String>) {
        val mappings = reader.readLines()
        for (line in mappings) {
            val halves = line.split(delimeter)
            map[byteFromHex(halves[0].substring(2))] = halves[1]
        }
    }

}