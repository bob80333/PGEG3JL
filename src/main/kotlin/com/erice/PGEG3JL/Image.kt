package com.erice.PGEG3JL

import javafx.geometry.Point2D
import java.awt.Color
import java.awt.image.BufferedImage

//todo test with this
//0x34fb84 -image
//0x3508bc -palettepointer

class Image(val imageData: ByteArray, val palette: Palette, val desiredSize: Point2D, val originalSize: Point2D) {
    fun get16ColorImage(transparency: Boolean): BufferedImage  {
        val image  = BufferedImage(originalSize.x.toInt(), originalSize.y.toInt(), BufferedImage.TYPE_INT_ARGB)
        var x = -1
        var y = 0
        var blockx = 0
        var blocky = 0
        for (i in 0 until imageData.size * 2) {
            if (x > 7) {
                x = 0
                y++
            }

            if(y > 7) {
                y = 0
                blockx++
            }
            if (blockx > (image.getWidth() / 8) - 1) {
                blockx = 0
                blocky++
            }
            var paletteIndex = imageData[i/2].toInt()
            if ((i and 1 ) == 0) {
                paletteIndex = paletteIndex and 0xF
            } else {
                paletteIndex = (paletteIndex and 0xF0) shr 4
            }

            val color = IntArray(4)
            color[0] = palette.reds[paletteIndex].toInt()
            color[1] = palette.greens[paletteIndex].toInt()
            color[2] = palette.blues[paletteIndex].toInt()
            color[3] = if (transparency && paletteIndex == 0) 0 else 255
            image.raster.setPixel(x + (blockx * 8), y + (blocky * 8), color)
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

        for (i in 0 until paletteData.size step 8) {
            val color = paletteData.slice(i..i+8).toByteArray().toLong();
            val r = ((color and 0x1F) shl 3).toInt()
            val g = ((color and 0x3E0) shr 2).toInt()
            val b = ((color and 0x7C00) shr 7).toInt()
            reds[i/8] = r.toByte()
            greens[i/8] = g.toByte()
            blues[i/8] = b.toByte()
            colors[i/8] = Color(r, g, b)
        }
    }
}