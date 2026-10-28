package com.eternity.fisherria

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlin.math.abs

abstract class PhysicsObject {
    protected abstract val texturePath: String

    open val width: Float = 1f
    open val height: Float = 1f
    open val gravity: Float = -30f

    protected lateinit var sprite: Sprite

    var verticalSpeed: Float = 0f
        protected set

    protected var currentBehavior: TileBehavior = EmptyBehavior
        private set

    private var justJumped: Boolean = false

    protected fun initSprite(x: Float, y: Float) {
        sprite = Sprite(Texture(texturePath))
        sprite.setSize(width, height)
        sprite.setPosition(x, y)
    }

    fun getX(): Float = sprite.x
    fun getY(): Float = sprite.y

    fun setX(x: Float) {
        sprite.x = x
    }

    fun setY(y: Float) {
        sprite.y = y
    }

    fun addVerticalForce(force: Float) {
        verticalSpeed += force
    }

    fun multiplyVerticalSpeed(factor: Float) {
        verticalSpeed *= factor
    }

    protected fun setJustJumped(value: Boolean) {
        justJumped = value
    }

    open fun update(delta: Float, map: GameMap) {
        val centerX = sprite.x + width / 2f
        val centerY = sprite.y + height / 2f

        val newBehavior = map.getBehaviorAt(centerX, centerY)

        if (newBehavior !== currentBehavior) {
            currentBehavior.onEntityExit(this)
            newBehavior.onEntityEnter(this)
            currentBehavior = newBehavior
        }

        // гравитация
        val gravityMult = newBehavior.getGravityMultiplier(this)
        verticalSpeed += gravity * gravityMult * delta

        // силы от тайла (buoyancy, drag и т.д.)
        newBehavior.onEntityStay(this, delta, map, gravity)

        // ограничение скорости
        if (!justJumped) {
            val max = newBehavior.getMaxVerticalSpeed(this)
            if (max != null) {
                verticalSpeed = verticalSpeed.coerceIn(-max, max)
            }
        } else if (verticalSpeed <= 0f) {
            justJumped = false
        }

        moveVertically(delta, map)
    }

    private fun moveVertically(delta: Float, map: GameMap) {
        val totalStep = verticalSpeed * delta
        val steps = abs(totalStep).toInt().coerceAtLeast(1)
        val stepSize = totalStep / steps

        for (i in 0 until steps) {
            val newY = sprite.y + stepSize
            if (collidesAt(sprite.x, newY, map)) {
                verticalSpeed = 0f
                break
            }
            sprite.y = newY
        }

        onAfterVerticalMove(map)
    }

    protected open fun onAfterVerticalMove(map: GameMap) {}

    protected fun collidesAt(x: Float, y: Float, map: GameMap): Boolean {
        val left = x + 0.05f
        val right = x + width - 0.05f
        val bottom = y + 0.05f
        val top = y + height - 0.05f

        for (tx in left.toInt()..right.toInt()) {
            for (ty in bottom.toInt()..top.toInt()) {
                if (map.isSolidAt(tx.toFloat(), ty.toFloat())) return true
            }
        }
        return false
    }

    fun draw(batch: SpriteBatch) {
        sprite.draw(batch)
    }

    open fun dispose() {
        sprite.texture.dispose()
    }

    companion object {
        const val WATER_BUOYANCY_COEFFICIENT = 1.4f // больше коэффициент - быстрее всплытие
        const val WATER_DRAG_COEFFICIENT = 1f       // больше коэффициент - быстрее гасится скорость
        const val WATER_MAX_VERTICAL_SPEED = 4f
        const val WATER_GRAVITY_MULTIPLIER = 0.2f
    }
}
