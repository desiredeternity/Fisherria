package com.eternity.fisherria

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import kotlin.math.sqrt

class Bobber(
    override val texturePath: String,
    startX: Float,
    startY: Float,
    targetX: Float,
    targetY: Float
): PhysicsObject() {
    override val width = 0.5f * 16/30
    override val height = 0.5f * 1
    override val gravity = -22f

    private var state = State.FLYING
    private var velocityX = 0f

    val isFlying: Boolean get() = state == State.FLYING
    val isInWater: Boolean get() = state == State.IN_WATER
    val isLanded: Boolean get() = state == State.LANDED

    init {
        initSprite(startX, startY)

        val dx = targetX - startX
        val dy = targetY - startY
        val distance = sqrt(dx * dx + dy * dy).coerceAtLeast(0.1f)

        val speed = (8f + distance * 0.9f).coerceIn(8f, 22f)

        velocityX = dx / distance * speed
        verticalSpeed = (dy / distance * speed * 0.6f + 7f).coerceAtLeast(6f)
    }

    override fun update(delta: Float, map: GameMap) {
        when (state) {
            State.FLYING -> updateFlying(delta, map)
            State.IN_WATER -> super.update(delta, map)
            State.LANDED -> {}
        }
    }

    private fun updateFlying(delta: Float, map: GameMap) {
        // Горизонталь
        setX(getX() + velocityX * delta)

        // Вертикаль + гравитация
        verticalSpeed += gravity * delta
        setY(getY() + verticalSpeed * delta)

        val centerX = getX() + width / 2f
        val centerY = getY() + height / 2f

        // Попали в воду?
        if (map.isLiquidAt(centerX, centerY)) {
            state = State.IN_WATER
            verticalSpeed *= 0.35f
            velocityX *= 0.15f
            return
        }

        // Ударились о землю?
        if (collidesAt(getX(), getY(), map)) {
            state = State.LANDED
            verticalSpeed = 0f
            velocityX = 0f
            return
        }

        // Улетел далеко вниз
        if (getY() < -5f) {
            state = State.LANDED
        }
    }

    private enum class State {
        FLYING,
        IN_WATER,
        LANDED
    }
}
