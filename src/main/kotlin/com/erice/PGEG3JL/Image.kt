package com.erice.PGEG3JL

import javafx.geometry.Point2D
import java.awt.Color

class Image(imageData: ByteArray, palette: Palette, desiredSize: Point2D, originalSize: Point2D) {

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