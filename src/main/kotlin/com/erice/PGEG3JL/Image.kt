package com.erice.PGEG3JL

import javafx.geometry.Point2D
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

//todo test with this
//0x37aa48 -image
//0x382a48 -palettepointer

class Image(val imageData: ByteArray, val palette: Palette, val desiredSize: Point2D, val originalSize: Point2D) {
    fun get16ColorImage(transparency: Boolean): BufferedImage  {
        val image  = BufferedImage(originalSize.x.toInt(), originalSize.y.toInt(), BufferedImage.TYPE_INT_ARGB)
        var x = -1
        var y = 0
        var blockx = 0
        var blocky = 0
        for (i in 0 until imageData.size * 2) {
            x++
            if (x >= 8) {
                x = 0
                y++
            }
            if (y >= 8) {
                y = 0
                blockx++
            }
            if (blockx > image.width / 8 - 1) {
                blockx = 0
                blocky++
            }

            var pal = imageData[i / 2].toPositiveInt()
            if ((i and 1) == 0)
                pal = pal and 0xF
            else
                pal = (pal and 0xF0) shr 4

            image.raster.setPixel(x + blockx * 8, y + blocky * 8, intArrayOf(palette.reds[pal].toPositiveInt(), palette.greens[pal].toPositiveInt(), palette.blues[pal].toPositiveInt(), if (transparency && pal == 0) 0 else 255))
        }

        return image
    }

}

enum class ImageType {
    Palette16Color,
    Palette256Color
}

class Palette(type: ImageType, paletteData: ByteArray) {
    val colors: Array<Color>
    val reds: ByteArray
    val greens: ByteArray
    val blues: ByteArray

    init {
        if (type == ImageType.Palette16Color) {
            colors = Array(16, {Color.WHITE})
            reds = ByteArray(16)
            greens = ByteArray(16)
            blues = ByteArray(16)
        } else {
            colors = Array(256, {Color.WHITE})
            reds = ByteArray(256)
            greens = ByteArray(256)
            blues = ByteArray(256)
        }

        for (i in 0 until paletteData.size step 2) {
            val color = paletteData.slice(i..i+1)
                    .toByteArray().toIntArray()
                    .toLong()
            //println("C" + color)
            val r = ((color and 0x1F) shl 3).toInt()
            val g = ((color and 0x3E0) shr 2).toInt()
            val b = ((color and 0x7C00) shr 7).toInt()
            reds[i/8] = r.toByte()
            greens[i/8] = g.toByte()
            blues[i/8] = b.toByte()
            //println("R" + r)
            //println("G" + g)
            //println("B" + b)
            colors[i/2] = Color(r, g, b)
        }
    }
}

fun main(args: Array<String>) {
    val image = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 D0 DD DD DD D0 FF FF FF C0 EE EE EE C0 3E 33 CC C0 3E C3 FF C0 3E FC CD C0 3E FC FC C0 3E FC 22 DD DD DD 00 FF FF DF 00 EE EE CE 00 3C 33 CE 00 2F 33 CE 00 12 32 CE 00 12 32 CE 00 12 32 CE 00 D0 DD DD DD D0 FF FF FF C0 44 44 44 C0 44 44 44 C0 64 6C 44 C0 64 6C 44 C0 64 6C 44 C0 44 66 44 DD DD DD 00 FF FF DF 00 44 44 C4 00 44 44 C4 00 64 6C C4 00 64 6C C4 00 64 6C C4 00 64 46 C4 00 D0 DD DD DD D0 FF FF FF C0 61 16 66 C0 11 66 61 C0 11 11 11 C0 C1 1F 11 C0 C1 1F 11 C0 C1 1C 11 DD DD DD 00 FF FF DF 00 16 66 C1 00 61 16 C1 00 11 11 C1 00 11 FC C1 00 11 FC C1 00 11 CC C1 00 D0 DD DD DD D0 FF FF FF C0 88 88 33 C0 88 88 33 C0 88 88 38 C0 88 F8 88 C0 11 C8 88 C0 18 81 88 DD DD DD 00 FF FF DF 00 83 88 C8 00 83 88 C8 00 88 88 C8 00 F8 88 C8 00 C8 18 C1 00 88 11 C8 00 D0 DD DD DD D0 11 11 11 C0 33 33 33 C0 31 33 33 C0 11 33 33 C0 31 11 11 C0 13 1F 11 C0 11 1C 11 DD DD DD DD 41 41 44 D4 33 33 33 C3 33 31 F3 C1 13 11 1F C1 31 31 1F C1 1F 33 11 C1 1C 31 13 C1 FC FF FF CF CF FF FF FC FF FC CF FF FF CF FC FF FF CF FC FF FF FC CF FF CF FF FF FC FC FF FF CF DD DD DD DD FD FF FF FF 8C 88 88 88 8C 88 88 88 8C 88 88 88 8C CC 8C 88 8C CC 86 88 8C 6C 66 66 DD DD DD DD FF FF FF FF 88 C8 CC 88 88 C8 66 66 88 88 68 6F CC 8C 66 6C C6 8C 16 66 66 8C 68 66 DD DD DD DD FF FF FF FF 88 CC 8C 88 66 66 8C 88 F6 86 88 88 C6 66 C8 CC 66 61 C8 6C 66 86 C8 66 DD DD DD DD FF FF FF DF 88 88 88 C8 88 88 88 C8 88 88 88 C8 88 C8 CC C8 88 68 CC C8 66 66 C6 C8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 3E 23 11 C0 3E 33 22 C0 EE EE EE C0 CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 21 33 CE 00 32 33 CE 00 EE EE CE 00 CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 46 44 4C C0 66 46 44 C0 66 66 66 C0 CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 4C 44 C6 00 44 66 C6 00 66 66 C6 00 CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 11 11 66 C0 11 61 66 C0 11 11 66 C0 CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 16 11 C1 00 66 11 C1 00 16 11 C1 00 CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 11 81 83 C0 18 F1 88 C0 11 FF FF C0 CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 83 11 C1 00 F8 11 C8 00 FF 1F C1 00 CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 F1 11 13 C0 11 11 11 C0 13 11 11 C0 11 31 33 C0 33 33 33 C0 CC CC CC 00 00 00 00 00 00 00 00 F1 31 33 C3 11 31 CC C3 11 33 3C C3 11 C1 3C C3 33 33 33 C3 CC CC CC CC 00 00 00 00 00 00 00 00 FC FF FF CF CF FF FF FC FF FC CF FF FF CF FC FF FF CF FC FF FF FC CF FF CF FF FF FC FC FF FF CF 8C 88 C6 C6 8C 68 61 66 4C 48 C6 CC 4C 44 66 66 CC CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 86 88 6B 6B 81 88 B6 6B 66 84 66 66 46 44 64 46 CC CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 B6 B6 88 68 B6 6B 88 18 66 66 84 66 64 46 44 64 CC CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00 6C 6C 88 C8 66 16 86 C8 CC 6C 84 C4 66 66 44 C4 CC CC CC CC 00 00 00 00 00 00 00 00 00 00 00 00"
    val imageData = image.split(" ").map { Integer.parseInt(it, 16).toByte() }.toByteArray()
    val palette = "CA 2A 5F 5B 1F 4B 5B 3A 0F 21 27 3D E5 30 A3 28 82 1C 9B 77 1F 2F 77 2E 9F 2D 18 21 FF 7F 00 00"
    val paletteData = palette.split(" ").map { Integer.parseInt(it, 16).toByte() }.toByteArray()
    val pal = Palette(ImageType.Palette16Color, paletteData)
    val im = Image(imageData, pal, Point2D(16.0, 16.0), Point2D(32.0, 32.0))
    ImageIO.write(im.get16ColorImage(false), "png", File("C:\\Users\\bob80\\Desktop\\image.png"))
}