package com.eternity.fisherria

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle

class GameMap(val tiles: Array<Array<TileType>>) {
    val width: Float get() = tiles[0].size * 1f
    val height: Float get() = tiles.size * 1f
    private val textures = mutableMapOf<TileType, Texture>()
    private val tileSize: Float = 1f

    fun getBehaviorAt(worldX: Float, worldY: Float): TileBehavior {
        val tx = worldX.toInt()
        val ty = worldY.toInt()
        if (ty !in tiles.indices || tx !in tiles[ty].indices) return GroundBehavior
        return TileBehaviors.of(tiles[ty][tx])
    }

    fun isSolidAt(worldX: Float, worldY: Float): Boolean {
        return getBehaviorAt(worldX, worldY).solid
    }

    fun isLiquidAt(worldX: Float, worldY: Float): Boolean {
        return getBehaviorAt(worldX, worldY).liquid
    }

    fun getHorizontalSpeedMultiplierAt(worldX: Float, worldY: Float): Float {
        return getBehaviorAt(worldX, worldY).horizontalSpeedMultiplier
    }

    // ищет самый верхний жидкий тайл в столбце и возвращает верхнюю границу. "Поверхность воды"
    fun getLiquidLevelAt(worldX: Float): Float? {
        val tx = worldX.toInt()
        if (tx !in tiles[0].indices) return null
        for (ty in tiles.indices.reversed()) {
            if (TileBehaviors.of(tiles[ty][tx]).liquid) {
                return (ty + 1).toFloat()
            }
        }
        return null
    }

    fun getCollisionRects(): List<Rectangle> {
        val result = mutableListOf<Rectangle>()
        for (y in tiles.indices) {
            for (x in tiles[y].indices) {
                if (TileBehaviors.of(tiles[y][x]).solid) {
                    result.add(Rectangle(x.toFloat(), y.toFloat(), 1f, 1f))
                }
            }
        }
        return result
    }

    fun loadTexture() {
        textures[TileType.GRASS] = Texture("grass.png")
        textures[TileType.WATER] = Texture("water2.png")
        textures[TileType.GROUND] = Texture("ground.png")
    }

    fun draw(batch: SpriteBatch) {
        for (y in tiles.indices) {
            for (x in tiles[y].indices) {
                val type = tiles[y][x]
                val texture = textures[type] ?: continue
                batch.draw(texture, x * tileSize, y * tileSize, tileSize, tileSize)
            }
        }
    }

    fun dispose() {
        textures.values.forEach { it.dispose() }
        textures.clear()
    }
}
