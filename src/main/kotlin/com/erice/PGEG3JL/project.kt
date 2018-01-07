package com.erice.PGEG3JL

import java.io.FileWriter

class Project(val name: String, val filename: String, val absoluteFolderPath: String, val absoluteOriginalRomPath: String, val game: Game) {
    private fun saveProjectFile() {
        val fileWriter = FileWriter(absoluteFolderPath + filename)
        fileWriter.append("name := $name\n")
        fileWriter.append("filename := $filename\n")
        fileWriter.append("absoluteFolderPath := $absoluteFolderPath\n")
        fileWriter.append("absoluteOriginalRomPath := $absoluteOriginalRomPath\n")
        fileWriter.append("game := ${game.gameID}")
        fileWriter.flush()
        fileWriter.close()
    }
}