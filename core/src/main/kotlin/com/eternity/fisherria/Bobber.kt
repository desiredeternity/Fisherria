package com.eternity.fisherria

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2

class Bobber(
    val texturePath: String,
    startX: Float,
    startY: Float,
    targetX: Float,
    targetY: Float
) {
    private lateinit var sprite: Sprite
    private val width: Float = 0.5f
    private val height: Float = 0.5f

    private var state: BobberState = BobberState.FLYING
    private var velocityX: Float = 0f
    private var velocityY: Float = 0f
    private var time: Float = 0f
    private val flightDuration: Float = 1f
    private val arcHeight: Float = 2f

    val isFloating: Boolean get() = state == BobberState.FLYING
    val isFinished: Boolean get() = state == BobberState.LANDED

    private val startPos = Vector2(startX, startY)
    private val targetPos = Vector2(targetX, targetY)

    private fun initSprite(initX: Float, initY: Float) {
        sprite = Sprite(Texture(texturePath))
        sprite.setSize(width, height)
        sprite.setPosition(initX, initY)
    }

    init {
        initSprite(startX, startY)
    }

    fun update(delta: Float, map: GameMap) {
        when (state) {
            BobberState.FLYING -> updateFlying(delta, map)
            BobberState.FLOATING -> updateFloating(delta)
            BobberState.LANDED -> {}
        }
    }

    private fun updateFlying(delta: Float, map: GameMap) {
        time += delta
        val t = (time / flightDuration).coerceIn(0f, 1f)

        val x = startPos.x + (targetPos.x - startPos.x) * t
        val baseY = startPos.y + (targetPos.y - startPos.y) * t
        val arc = 4f * arcHeight * t * (1f - t)
        sprite.setPosition(x, baseY + arc)

        if (t >= 1f) {
            // Долетели — проверяем, вода ли в целевой точке
            if (map.isLiquidAt(targetPos.x, targetPos.y)) {
                state = BobberState.FLOATING
                sprite.setPosition(targetPos.x, targetPos.y)
            } else {
                // Не вода — падаем на землю и исчезаем
                state = BobberState.LANDED
                sprite.setPosition(targetPos.x, targetPos.y)
            }
        }
    }

    private fun updateFloating(delta: Float) {
        time += delta
    }

    fun draw(batch: SpriteBatch) {
        sprite.draw(batch)
    }

    fun dispose() {
        sprite.texture.dispose()
    }
}
