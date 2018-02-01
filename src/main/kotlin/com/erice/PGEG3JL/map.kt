package com.erice.PGEG3JL

// Most of the information here that is in code can be found here: http://datacrystal.romhacking.net/wiki/Pokémon_3rd_Generation
class Bank (val bankIndex: Byte, val mapIndex: Byte, val rom: Rom, val game: Game){

}

class Map {

}

class MapHeader(val rom: Rom, val game: Game, bankNumber: Int, val pointer: Int) {
    val mapPointer: Int  // pointer to map data
    val eventPointer: Int //pointer to event data
    val scriptsPointer: Int // pointer to scripts that the map runs
    val connectionPointer: Int // pointer to connection data
    val musicIndex: Short // index for music (little endian)
    val mapPointerIndex: Short // what I found says it may be a map pointer index. (little endian)
    val labelIndex: Byte
    val visibility: Byte // like a dark cave where HM flash might be needed
    val weather: Byte
    val mapType: Byte // like city, village, etc
    val unknown: Short //unknown, little endian.
    val showLabelOnEntry: Byte //show the map label on entry to the map
    val inBattleFieldModelId: Byte // which field background to use in battle?

    init {
        val data = GameData(game)

        var offsetFromBeginning = 0

        mapPointer = rom.getBytes(pointer + offsetFromBeginning, FULL_POINTER_BYTES).toInt()
        offsetFromBeginning += FULL_POINTER_BYTES

        eventPointer = rom.getBytes(pointer + offsetFromBeginning, FULL_POINTER_BYTES).toInt()
        offsetFromBeginning += FULL_POINTER_BYTES

        scriptsPointer = rom.getBytes(pointer + offsetFromBeginning, FULL_POINTER_BYTES).toInt()
        offsetFromBeginning += FULL_POINTER_BYTES

        connectionPointer = rom.getBytes(pointer + offsetFromBeginning, FULL_POINTER_BYTES).toInt()
        offsetFromBeginning += FULL_POINTER_BYTES

        musicIndex = rom.getBytes(pointer + offsetFromBeginning, SHORT_BYTES).toInt().toShort()
        offsetFromBeginning += SHORT_BYTES

        mapPointerIndex = rom.getBytes(pointer + offsetFromBeginning, SHORT_BYTES).toInt().toShort()
        offsetFromBeginning += SHORT_BYTES

        labelIndex = rom.getByte(pointer + offsetFromBeginning++)
        visibility = rom.getByte(pointer + offsetFromBeginning++)
        weather = rom.getByte(pointer + offsetFromBeginning++)
        mapType = rom.getByte(pointer + offsetFromBeginning++)

        unknown = rom.getBytes(pointer + offsetFromBeginning, SHORT_BYTES).toInt().toShort()
        offsetFromBeginning += SHORT_BYTES

        showLabelOnEntry = rom.getByte(pointer + offsetFromBeginning++)
        inBattleFieldModelId = rom.getByte(pointer + offsetFromBeginning)
    }
}

class MapLayout(val game: Game, val rom: Rom, val pointer: Int) {
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

        borderPointer = rom.getBytes(pointer + offsetFromBeginning, FULL_POINTER_BYTES).toInt()
        offsetFromBeginning += FULL_POINTER_BYTES

        mapDataPointer = rom.getBytes(pointer + offsetFromBeginning, FULL_POINTER_BYTES).toInt()
        offsetFromBeginning += FULL_POINTER_BYTES

        globalTilesetPointer = rom.getBytes(pointer + offsetFromBeginning, FULL_POINTER_BYTES).toInt()
        offsetFromBeginning += FULL_POINTER_BYTES

        localTilesetPointer = rom.getBytes(pointer + offsetFromBeginning, FULL_POINTER_BYTES).toInt()
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

data class ConnectionHeader(val mapConnectionCount: Int, val connectionDataPointer: Int) // little endian

class MapData(val game: Game, val rom: Rom) {
    //each entry encodes tile number and attribute.  each entry is 16-bit.
    //64 attributes, 512 tiles
    // in Ruby, FireRed, and Emerald, the 16 bits are split up 6:10 instead of 8:8
    //border data is the same
}

class TilesetHeader(val rom: Rom) {
    //val compressed: Byte
    //val isPrimary: Byte
    //val unknown: Byte
    //val unknown2: Byte
    //val tilesetImagePointer: Int (full size pointer), little endian
    //val colorPalettePointer: Int (full size pointer), little endian
    //val blockPointer: Int (full size pointer), little endian
    //val animationPointer: Int (full size pointer), little endian, null if no animation exists
    //val behaviorAndBackgroundPointer: Int (full size pointer), little endian
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
class ConnectionData(val rom: Rom) {
    //val connectionDirection: ConnectionDirection // little endian
    //val offset: Int // little endian, in reference to connecting map
    //val mapBank: Byte
    //val mapNumber: Byte
    //val filler: Short // little endian

    // the filler makes each piece of connection data take 12 bytes, aligning it to some better multiple of 2?
    // the filler makes it aligned to a multiple of 4 bytes
}