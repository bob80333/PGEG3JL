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
            if (byte.toString(16) == "FF") {
                return result
            }
            result += textDecode[byte]
        }

        return result
    }

    fun decodeStrings(bytes: ByteArray): MutableList<String> {
        val outputList = mutableListOf<String>()
        var string = ""
        bytes.forEach {
            if (it == byteFromHex("FF")) {
                outputList.add(string)
                string = ""
            } else {
                string += textDecode[it]
            }
        }

        return outputList
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

fun byteStringToByteArray(text: String): ByteArray {
    val bytes = text.split(" ")
    return bytes.map { Integer.parseInt(it, 16).toByte() }.toByteArray()
}

fun main(args: Array<String>) {
    val decode = PokeTextDecoder(Game.EmeraldENG)
    println(decode.decodeText(byteStringToByteArray("C6 C3 CE CE C6 BF CC C9 C9 CE 00 CE C9 D1 C8 FF C9 C6 BE BB C6 BF 00 CE C9 D1 C8 FF BE BF D1 C0 C9 CC BE 00 CE C9 D1 C8 FF C6 BB D0 BB CC C3 BE C1 BF 00 CE C9 D1 C8 FF C0 BB C6 C6 BB CC BC C9 CC 00 CE C9 D1 C8 FF D0 BF CC BE BB C8 CE CF CC C0 00 CE C9 D1 C8 FF CA BB BD C3 C0 C3 BE C6 C9 C1 00 CE C9 D1 C8 FF CA BF CE BB C6 BC CF CC C1 00 BD C3 CE D3 FF CD C6 BB CE BF CA C9 CC CE 00 BD C3 CE D3 FF C7 BB CF D0 C3 C6 C6 BF 00 BD C3 CE D3 FF CC CF CD CE BC C9 CC C9 00 BD C3 CE D3 FF C0 C9 CC CE CC BF BF 00 BD C3 CE D3 FF C6 C3 C6 D3 BD C9 D0 BF 00 BD C3 CE D3 FF C7 C9 CD CD BE BF BF CA 00 BD C3 CE D3 FF CD C9 C9 CE C9 CA C9 C6 C3 CD 00 BD C3 CE D3 FF BF D0 BF CC 00 C1 CC BB C8 BE BF 00 BD C3 CE D3 FF CC C9 CF CE BF 00 A2 A1 A2 FF CC C9 CF CE BF 00 A2 A1 A3 FF CC C9 CF CE BF 00 A2 A1 A4 FF CC C9 CF CE BF 00 A2 A1 A5 FF CC C9 CF CE BF 00 A2 A1 A6 FF CC C9 CF CE BF 00 A2 A1 A7 FF CC C9 CF CE BF 00 A2 A1 A8 FF CC C9 CF CE BF 00 A2 A1 A9 FF CC C9 CF CE BF 00 A2 A1 AA FF CC C9 CF CE BF 00 A2 A2 A1 FF CC C9 CF CE BF 00 A2 A2 A2 FF CC C9 CF CE BF 00 A2 A2 A3 FF CC C9 CF CE BF 00 A2 A2 A4 FF CC C9 CF CE BF 00 A2 A2 A5 FF CC C9 CF CE BF 00 A2 A2 A6 FF CC C9 CF CE BF 00 A2 A2 A7 FF CC C9 CF CE BF 00 A2 A2 A8 FF CC C9 CF CE BF 00 A2 A2 A9 FF CC C9 CF CE BF 00 A2 A2 AA FF CC C9 CF CE BF 00 A2 A3 A1 FF CC C9 CF CE BF 00 A2 A3 A2 FF CC C9 CF CE BF 00 A2 A3 A3 FF CC C9 CF CE BF 00 A2 A3 A4 FF CC C9 CF CE BF 00 A2 A3 A5 FF CC C9 CF CE BF 00 A2 A3 A6 FF CC C9 CF CE BF 00 A2 A3 A7 FF CC C9 CF CE BF 00 A2 A3 A8 FF CC C9 CF CE BF 00 A2 A3 A9 FF CC C9 CF CE BF 00 A2 A3 AA FF CC C9 CF CE BF 00 A2 A4 A1 FF CC C9 CF CE BF 00 A2 A4 A2 FF CC C9 CF CE BF 00 A2 A4 A3 FF CC C9 CF CE BF 00 A2 A4 A4 FF CC C9 CF CE BF 00 A2 A4 A5 FF CF C8 BE BF CC D1 BB CE BF CC FF C1 CC BB C8 C3 CE BF 00 BD BB D0 BF FF C7 CE AD 00 BD C2 C3 C7 C8 BF D3 FF CD BB C0 BB CC C3 00 D4 C9 C8 BF FF BC BB CE CE C6 BF 00 C0 CC C9 C8 CE C3 BF CC FF CA BF CE BB C6 BC CF CC C1 00 D1 C9 C9 BE CD FF CC CF CD CE CF CC C0 00 CE CF C8 C8 BF C6 FF BB BC BB C8 BE C9 C8 BF BE 00 CD C2 C3 CA FF C8 BF D1 00 C7 BB CF D0 C3 C6 C6 BF FF C7 BF CE BF C9 CC 00 C0 BB C6 C6 CD FF C7 CE AD 00 CA D3 CC BF FF FD 08 00 C2 C3 BE BF C9 CF CE FF CD C2 C9 BB C6 00 BD BB D0 BF FF CD BF BB C0 C6 C9 C9 CC 00 BD BB D0 BF CC C8 FF D0 C3 BD CE C9 CC D3 00 CC C9 BB BE FF C7 C3 CC BB C1 BF 00 C3 CD C6 BB C8 BE FF BD BB D0 BF 00 C9 C0 00 C9 CC C3 C1 C3 C8 FF CD C9 CF CE C2 BF CC C8 00 C3 CD C6 BB C8 BE FF C0 C3 BF CC D3 00 CA BB CE C2 FF C4 BB C1 C1 BF BE 00 CA BB CD CD FF CD BF BB C6 BF BE 00 BD C2 BB C7 BC BF CC FF CD BD C9 CC BD C2 BF BE 00 CD C6 BB BC FF C3 CD C6 BB C8 BE 00 BD BB D0 BF FF BE BF CD BF CC CE 00 CC CF C3 C8 CD FF BB C8 BD C3 BF C8 CE 00 CE C9 C7 BC FF C3 C8 CD C3 BE BF 00 C9 C0 00 CE CC CF BD C5 FF CD C5 D3 00 CA C3 C6 C6 BB CC FF CD BF BD CC BF CE 00 BC BB CD BF FF FF CA BB C6 C6 BF CE 00 CE C9 D1 C8 FF D0 C3 CC C3 BE C3 BB C8 00 BD C3 CE D3 FF CA BF D1 CE BF CC 00 BD C3 CE D3 FF BD")))
}