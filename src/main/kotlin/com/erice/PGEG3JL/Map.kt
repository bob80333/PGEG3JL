package com.erice.PGEG3JL

import java.io.File
import java.io.FileInputStream

// Most of the information here that is in code can be found here: http://datacrystal.romhacking.net/wiki/Pokémon_3rd_Generation

// None of the pointers here are full pointers, because they only reference data on the ROM.  In the code in the ROM,
// the pointers will be full-length to access all the memory.
class Banks(val rom: Rom, val game: Game, gameData: GameData) {
    val banks: Array<Bank>
    val mapNames: Array<String>
    init {
        val mapNamesPointer = gameData.getGameDataPiece("map_names", "0x0").substring(2).toInt(16)
        val mapNamesLength = gameData.getGameDataPiece("map_names_length", "0").toInt()
        mapNames = Array(mapNamesLength, {""})
        loadMapNames(mapNamesPointer, mapNamesLength)

        val bankListPointer = gameData.getGameDataPiece("pointer_to_map_bank_pointer_table", "0x0").substring(2).toInt(16)
        val numBanks = gameData.getGameDataPiece("num_banks", "0x0").substring(2).toInt(16)
        banks = Array(numBanks, {Bank(it, rom, game, rom.getPointer(bankListPointer) + (it * 4), gameData, mapNames)})
    }

    private fun loadMapNames(pointer: Int, length: Int) {
        val data = rom.getBytes(pointer, length)
        val textDecoder = PokeTextDecoder(game)
        textDecoder.decodeStrings(data).forEachIndexed { index, s ->
            mapNames[index] = s
        }
    }
}

class Bank (val bankIndex: Int, val rom: Rom, val game: Game, val pointer: Int, gameData: GameData, val mapNames: Array<String>) {
    val numMaps: Int
    val maps: Array<Map>
    init {
        numMaps = gameData.getGameDataPiece("num_maps_bank", "0").split(",")[bankIndex].toInt()
        maps = Array(numMaps, {Map(it, rom, game, rom.getPointer(pointer) + (it * 4), mapNames)})
    }
}

class Map(val mapIndex: Int, val rom: Rom, val game: Game, val pointer: Int, val mapNames: Array<String>) {
    val header: MapHeader
    val layout: MapLayout
    val connectionHeader: ConnectionHeader
    val tileData: Array<MapTileData>
    val globalTileset: TilesetHeader
    val localTileset: TilesetHeader
    val connectionData: Array<ConnectionData>
    val name: String
    init {
        header = MapHeader(rom, game, rom.getPointer(pointer))
        layout = MapLayout(rom, game, header.mapPointer)
        connectionHeader = ConnectionHeader(rom, game, header.connectionPointer)
        globalTileset = TilesetHeader(rom, layout.globalTilesetPointer.toROMPointer())
        localTileset = TilesetHeader(rom, layout.localTilesetPointer.toROMPointer())

        tileData = Array(layout.heightTiles.value * layout.widthTiles.value, {MapTileData(rom, game, it, true)})
        loadTiles(layout.heightTiles.value, layout.widthTiles.value)


        if (connectionHeader.pointer != 0) {
            connectionData = Array(connectionHeader.numConnections.getLE(), { ConnectionData(rom, it, true) })
            loadConnectionData(connectionHeader.numConnections.value, connectionHeader.pointer)
        } else {
            connectionData = Array(0, { ConnectionData(rom, it, true) })
        }

        name = mapNames[header.labelIndex.toPositiveInt()]
    }

    private fun loadTiles(height: Int, width: Int) {
        for (i in 0 until height * width) {
            tileData[i] = MapTileData(rom, game, layout.mapDataPointer + (i * SHORT_BYTES))
        }
    }

    private fun loadConnectionData(numConnections: Int, connectionPointer: Int) {
        for (i in 0 until numConnections) {
            connectionData[i] = ConnectionData(rom, connectionPointer + (i * 12))
        }
    }

}

class MapHeader(val rom: Rom, val game: Game, val pointer: Int) {
    val mapPointer: Int  // pointer to map data
    val eventPointer: Int //pointer to event data
    val scriptsPointer: Int // pointer to scripts that the map runs
    val connectionPointer: Int // pointer to connection data
    val musicIndex: Char // index for music (little endian)
    val mapPointerIndex: Char // what I found says it may be a map pointer index. (little endian)
    val labelIndex: Byte
    val visibility: Byte // like a dark cave where HM flash might be needed
    val weather: Byte
    val mapType: Byte // like city, village, etc
    val unknown: Char //unknown, little endian.
    val showLabelOnEntry: Byte //show the map label on entry to the map
    val inBattleFieldModelId: Byte // which field background to use in battle?

    init {
        val data = GameData(game)

        var offsetFromBeginning = 0

        mapPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        eventPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        scriptsPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        connectionPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        musicIndex = rom.getBytes(pointer + offsetFromBeginning, SHORT_BYTES).toInt().toChar()
        offsetFromBeginning += SHORT_BYTES

        mapPointerIndex = rom.getBytes(pointer + offsetFromBeginning, SHORT_BYTES).toInt().toChar()
        offsetFromBeginning += SHORT_BYTES

        labelIndex = rom.getByte(pointer + offsetFromBeginning++)
        visibility = rom.getByte(pointer + offsetFromBeginning++)
        weather = rom.getByte(pointer + offsetFromBeginning++)
        mapType = rom.getByte(pointer + offsetFromBeginning++)

        unknown = rom.getBytes(pointer + offsetFromBeginning, SHORT_BYTES).toInt().toChar()
        offsetFromBeginning += SHORT_BYTES

        showLabelOnEntry = rom.getByte(pointer + offsetFromBeginning++)
        inBattleFieldModelId = rom.getByte(pointer + offsetFromBeginning)
    }
}

class MapLayout(val rom: Rom, val game: Game, val pointer: Int) {
    val widthTiles: IntLE // little endian
    val heightTiles: IntLE  // little endian
    val borderPointer: Int
    val mapDataPointer: Int // aka Tile Structure
    val globalTilesetPointer: Int
    val localTilesetPointer: Int

    // border width and border height only exist in FireRed/LeafGreen, in Ruby/Sapphire/Emerald it is missing
    // and borders are always 2x2 squares

    val borderWidth: Byte
    val borderHeight: Byte

    init {
        var offsetFromBeginning = 0

        widthTiles = IntLE(rom.getBytes(pointer + offsetFromBeginning, INT_BYTES).toInt())
        offsetFromBeginning += INT_BYTES

        heightTiles = IntLE(rom.getBytes(pointer + offsetFromBeginning, INT_BYTES).toInt())
        offsetFromBeginning += INT_BYTES

        borderPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        mapDataPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        globalTilesetPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        localTilesetPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        if (game.gameId.startsWith("BPR") || game.gameId.startsWith("BPF")) {
            borderWidth = rom.getByte(pointer + offsetFromBeginning++)
            borderHeight = rom.getByte(pointer + offsetFromBeginning)
        } else {
            borderWidth = 2
            borderHeight = 2
        }
    }
}

class ConnectionHeader(val rom: Rom, val game: Game, val pointer: Int) {
    val numConnections: IntLE = IntLE(rom.getBytes(pointer, INT_BYTES).toInt())
    val connectionDataPointer: Int

    init {
        val offsetFromBeginning = INT_BYTES
        connectionDataPointer = rom.getPointer(pointer + offsetFromBeginning)
    }
}

class MapTileData(val rom: Rom, val game: Game, val pointer: Int, val arrayInitializer: Boolean = false) {
    //each entry encodes tile number and attribute.  each entry is 16-bit.
    //64 attributes, 512 tiles
    // in Ruby, FireRed, and Emerald, the 16 bits are split up 6:10 instead of 8:8
    //border data is the same

    val attribute: Byte
    val tileNum: Char

    init {
        if (!arrayInitializer) {
            val data = rom.getBytes(pointer, 2).toInt()

            if (game.gameId.startsWith("AXV") || game.gameId.startsWith("BPE") || game.gameId.startsWith("BPR")) {
                attribute = (data and 0xFC00).shr(10).toByte()
                tileNum = (data and 0x03FF).toChar()
            } else {
                attribute = (data and 0xFF00).shr(8).toByte()
                tileNum = (data and 0x00FF).toChar()
            }
        } else {
            attribute = 0
            tileNum = '0'
        }
    }
}

class TilesetHeader(val rom: Rom, pointer: Int) {
    val compressed: Byte
    val isPrimary: Byte
    val unknown: Byte
    val unknown2: Byte
    val tilesetImagePointer: Int
    val colorPalettePointer: Int
    val blockPointer: Int
    val animationPointer: Int //  null if no animation exists
    val behaviorAndBackgroundPointer: Int

    init {
        var offsetFromBeginning = 0
        compressed = rom.getByte(pointer + offsetFromBeginning++)
        isPrimary = rom.getByte(pointer + offsetFromBeginning++)
        unknown = rom.getByte(pointer + offsetFromBeginning++)
        unknown2 = rom.getByte(pointer + offsetFromBeginning++)

        tilesetImagePointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        colorPalettePointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        blockPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        animationPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES

        behaviorAndBackgroundPointer = rom.getPointer(pointer + offsetFromBeginning)
        offsetFromBeginning += FULL_POINTER_BYTES
    }
}

class Tileset(val mapLayout: MapLayout, val tilesetHeader: TilesetHeader, val rom: Rom) {

}
enum class ConnectionDirection(val direction: IntLE) {
    NoConnection(IntLE(0x0)),
    Down(IntLE(0x1)),
    Up(IntLE(0x2)),
    Left(IntLE(0x3)),
    Right(IntLE(0x4)),
    Dive(IntLE(0x5)),
    Emerge(IntLE(0x5));
}

object ConnectionDirectionFromIntLE {
    private val byCode = mutableMapOf<IntLE, ConnectionDirection>()
    init {
        for (code in ConnectionDirection.values()) {
            byCode[code.direction] = code
        }
    }

    fun get(code: IntLE, default: ConnectionDirection = ConnectionDirection.NoConnection): ConnectionDirection {
        return byCode.getOrDefault(code, default)
    }
}

class ConnectionData(val rom: Rom, val pointer: Int, val arrayInitializer: Boolean = false) {
    val connectionDirection: ConnectionDirection // little endian
    val offset: IntLE // little endian, in reference to connecting map
    val mapBank: Byte
    val mapNumber: Byte
    val filler: Char // little endian

    init {
        if (!arrayInitializer) {
            var offsetFromBeginning = 0

            connectionDirection = ConnectionDirectionFromIntLE.get(IntLE(rom.getBytes(pointer, INT_BYTES).toInt()))
            offsetFromBeginning += INT_BYTES

            offset = IntLE(rom.getBytes(pointer + offsetFromBeginning, INT_BYTES).toInt())
            offsetFromBeginning += INT_BYTES

            mapBank = rom.getByte(pointer + offsetFromBeginning++)
            mapNumber = rom.getByte(pointer + offsetFromBeginning++)

            filler = rom.getBytes(pointer + offsetFromBeginning, SHORT_BYTES).toInt().toChar()
            offsetFromBeginning += SHORT_BYTES
        } else {
            connectionDirection = ConnectionDirection.NoConnection
            offset = IntLE(0)
            mapBank = 0
            mapNumber = 0
            filler = '0'
        }
    }

    // the filler makes each piece of connection data take 12 bytes, aligning it to some better multiple of 2?
    // the filler makes it aligned to a multiple of 4 bytes
}

fun main(args: Array<String>) {
    val romFile = FileInputStream(File("E:\\Downloads\\1986 - Pokemon Emerald (U)(TrashMan)\\1986 - Pokemon Emerald (U)(TrashMan).gba"))
    val romData = ByteArray(16 * MB)
    romFile.read(romData)
    val game = Game.EmeraldENG
    val gameData = GameData(game)

    val rom = Rom("emerald", romData)
    val banks = Banks(rom, game, gameData)

    banks.banks.forEach {
        it.maps.filter { it.globalTileset.compressed.toPositiveInt() == 0 }
                .forEach {
                    println(it.globalTileset.tilesetImagePointer.toHexString())
                    println(it.globalTileset.colorPalettePointer.toHexString())
                    println(it.localTileset.tilesetImagePointer.toHexString())
                    println(it.localTileset.colorPalettePointer.toHexString())
                }
    }
}