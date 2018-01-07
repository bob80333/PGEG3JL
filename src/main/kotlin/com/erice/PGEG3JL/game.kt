package com.erice.PGEG3JL

import java.io.InputStreamReader

enum class Game(val gameId: String) {
    EmeraldENG("BPEE"),
    EmeraldJPN("BPEJ"),
    AutoDetect("AUTODETECT")
}

object CodeToGame {
    private val byCodeHashmap: Map<String, Game>
    init {
        val byCodeHashmap = mutableMapOf<String, Game>()
        for (value in Game.values()) {
            byCodeHashmap[value.gameId] = value
        }

        this.byCodeHashmap = byCodeHashmap.toMap()
    }

    fun getGameFromCode(code: String): Game {
        return byCodeHashmap.getOrDefault(code, Game.AutoDetect)
    }
}

fun findGame(rom: Rom): Game {
    return CodeToGame.getGameFromCode(rom.getBytes(0xAC, 4).toAsciiString())
}

class PokeTextDecoder(val game: Game) {
    val textDecode = mutableMapOf<Byte, String>()
    private val delimiter = " := "

    init {
        if (game == Game.EmeraldENG) {
            loadEmeraldDecodeTable(textDecode)
        }
    }

    fun decodeText(bytes: ByteArray): String {
        var result = ""
        for (byte in bytes) {
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