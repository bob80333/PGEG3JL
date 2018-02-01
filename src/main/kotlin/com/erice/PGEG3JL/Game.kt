package com.erice.PGEG3JL

import java.io.InputStreamReader

enum class Game(val gameId: String) {
    EmeraldENG("BPEE"),
    RubyENG("AXVE"),
    SapphireENG("AXPE"),
    FireRedENG("BPRE"),
    LeafGreenENG("BPGE"),
    EmeraldFRA("BPEF"),
    RubyFRA("AXVF"),
    SapphireFRA("AXPF"),
    FireRedFRA("BPRF"),
    LeafGreenFRA("BPGF"),
    EmeraldITA("BPEE"),
    RubyITA("AXVI"),
    SapphireITA("AXPI"),
    FireRedITA("BPRI"),
    LeafGreenITA("BPGI"),
    EmeraldJPN("BPEJ"),
    AutoDetect("AUTODETECT")
}

object CodeToGame {
    private val byCodeHashmap: MutableMap<String, Game>
    init {
        val byCodeHashmap = mutableMapOf<String, Game>()
        for (value in Game.values()) {
            byCodeHashmap[value.gameId] = value
        }

        this.byCodeHashmap = byCodeHashmap.toMutableMap()
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
        val reader = InputStreamReader(javaClass.classLoader.getResourceAsStream("BPEE_text_encoding.txt"))
        loadDecodeTable(reader, map)
    }

    private fun loadDecodeTable(reader: InputStreamReader, map: MutableMap<Byte, String>) {
        val mappings = reader.readLines()
        mappings
            .map { it.split(delimiter) }
            .forEach { map[byteFromHex(it[0].substring(2))] = it[1] }
    }

}

class GameData(game: Game) {
    val data = mutableMapOf<String, String>()
    private val delimiter = " := "

    init {
        loadData(game.gameId + "_data.txt")
    }

    private fun loadData(location: String) {
        val reader = InputStreamReader(javaClass.classLoader.getResourceAsStream(location))
        val lines = reader.readLines()
        lines
            .map { it.split(delimiter) }
            .forEach { data[it[0]] = it[1] }
    }

    fun getGameDataPiece(label: String, default: String) : String {
        return data.getOrDefault(label, default)
    }
}