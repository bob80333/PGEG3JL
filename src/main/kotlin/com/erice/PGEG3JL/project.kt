package com.erice.PGEG3JL

import java.io.FileReader
import java.io.FileWriter

class Project(val name: String, val filename: String, val absoluteFolderPath: String, val absoluteOriginalRomPath: String, val game: Game) {

    companion object {
        private val delimiter = " := "
        fun loadProject(projectFilePath: String): Project {
            val fileReader = FileReader(projectFilePath)
            val lines = fileReader.readLines()

            val name = lines[0].split(delimiter)[1]
            val fileName = lines[1].split(delimiter)[1]
            val absoluteFolderPath = lines[2].split(delimiter)[1]
            val absoluteOriginalRomPath = lines[3].split(delimiter)[1]
            val game = Game.valueOf(lines[4].split(delimiter)[1])

            return Project(name, fileName, absoluteFolderPath, absoluteOriginalRomPath, game)
        }
    }

    private fun saveProjectFile() {
        val fileWriter = FileWriter(absoluteFolderPath + filename)
        fileWriter.write("name := $name\n")
        fileWriter.write("filename := $filename\n")
        fileWriter.write("absoluteFolderPath := $absoluteFolderPath\n")
        fileWriter.write("absoluteOriginalRomPath := $absoluteOriginalRomPath\n")
        fileWriter.write("game := ${game.gameID}")
        fileWriter.flush()
        fileWriter.close()
    }


}