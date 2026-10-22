package com.eternity.fisherria

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap

object MapLoader {
    fun loadFromPng(path: String): Array<Array<TileType>> {
        val pixmap = Pixmap(Gdx.files.internal(path))
        val width = pixmap.width
        val height = pixmap.height

        val tiles = Array(height) { Array(width) { TileType.EMPTY } }

        for (py in 0 until height) {
            for (px in 0 until width) {
                val color = pixmap.getPixel(px, py)
                tiles[height - 1 - py][px] = colorToTile(color)
            }
        }

        pixmap.dispose()
        return tiles
    }

    private fun colorToTile(color: Int): TileType {
        val r = (color ushr 24) and 0xFF
        val g = (color ushr 16) and 0xFF
        val b = (color ushr 8) and 0xFF
        val a = color and 0xFF

        if (a == 0) return TileType.EMPTY

        return when {
            r == 0 && g == 255 && b == 0 -> TileType.GRASS
            r == 0 && g == 0 && b == 255 -> TileType.WATER
            r == 160 && g == 100 && b == 70 -> TileType.GROUND
            else -> TileType.EMPTY
        }
    }
}
