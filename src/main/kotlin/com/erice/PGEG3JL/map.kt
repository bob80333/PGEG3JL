package com.erice.PGEG3JL

class Map {

}

class MapHeader(val rom: Rom, val game: Game, bankNumber: Int) {
    val mapPointer: Int
    //val spritesPointer: Int
    //val scriptPointer: Int
    //val connectionPointer: Int

    init {
        val data = GameData(game)
        mapPointer = rom.getBytes(data.getGameDataPiece("bank_pointer_" + bankNumber, "").toInt(16),3).toInt()
    }
}