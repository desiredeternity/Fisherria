package com.eternity.fisherria

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import kotlin.math.abs

abstract class Entity {
    abstract var name: String
        protected set

    protected abstract val texturePath: String
    protected open val width: Float = 1f
    protected open val height: Float = 1f
    protected open val horizontalSpeed: Float = 8f
    protected open val jumpPower: Float = 12f
    protected open val gravity: Float = -30f
    protected lateinit var sprite: Sprite
    private var facingRight: Boolean = false

    protected open var verticalSpeed: Float = 0f
    protected var isOnGround: Boolean = true
        private set

    protected var isInWater: Boolean = false
        private set
    private var justJumped = false

    protected fun initSprite(initX: Float, initY: Float) {
        sprite = Sprite(Texture(texturePath))
        sprite.setSize(width, height)
        sprite.setPosition(initX, initY)
    }

    fun getX(): Float = sprite.x
    fun getY(): Float = sprite.y

    fun setX(x: Float) {
        sprite.x = x
    }

    fun setY(y: Float) {
        sprite.y = y
    }

    fun move(direction: Direction, delta: Float, worldWidth: Float, map: GameMap) {
        val tileBehavior = map.getBehaviorAt(sprite.x + width / 2f, sprite.y + height / 2f)
        val tileMultiplier = tileBehavior.horizontalSpeedMultiplier
        val waterMultiplier = if (isInWater) 0.5f else 1f
        val speedMultiplier = tileMultiplier * waterMultiplier

        val newX = when (direction) {
            Direction.LEFT -> {
                if (facingRight) {
                    sprite.flip(true, false)
                    facingRight = false
                }
                sprite.x - horizontalSpeed * speedMultiplier * delta
            }
            Direction.RIGHT -> {
                if (!facingRight) {
                    sprite.flip(true, false)
                    facingRight = true
                }
                sprite.x + horizontalSpeed * speedMultiplier * delta
            }
        }

        if (!collidesAt(newX, sprite.y, map)) {
            sprite.x = newX
        }

        sprite.x = MathUtils.clamp(sprite.x, 0f, worldWidth - width)
    }

    fun jump() {
        if (isInWater) {
            verticalSpeed = jumpPower * 0.8f
            justJumped = true
            return
        }

        if (!isOnGround) return
        verticalSpeed = jumpPower
        isOnGround = false
    }

    fun update(delta: Float, map: GameMap) {
        val centerX = sprite.x + width / 2f
        val centerY = sprite.y + height / 2f
        isInWater = map.isLiquidAt(centerX, centerY)

        if (isInWater) {
            updateInWater(delta, map)
        } else {
            updateInAir(delta, map)
        }
    }

    private fun updateInWater(delta: Float, map: GameMap) {
        val waterGravity = gravity * WATER_GRAVITY_MULTIPLIER
        verticalSpeed += waterGravity * delta

        val surfaceY = map.getLiquidLevelAt(sprite.x + width / 2f)
        if (surfaceY != null) {
            val submergedDepth = surfaceY - sprite.y // глубина погружения объекта в воду

            if (submergedDepth > 0f) {
                // постоянное выталкивание
                verticalSpeed += -waterGravity * WATER_BUOYANCY_COEFFICIENT * delta

                // сопротивление воды
                val drag = 1f - WATER_DRAG_COEFFICIENT * delta
                verticalSpeed *= drag.coerceAtLeast(0f)
            }
        }

        if (!justJumped) { // ограничение вертикальной скорости
            verticalSpeed = verticalSpeed.coerceIn(-WATER_MAX_VERTICAL_SPEED, WATER_MAX_VERTICAL_SPEED)
        } else {
            if (verticalSpeed <= 0f) justJumped = false // прыжок ещё действует, не обрезаем
        }

        moveVertically(delta, map)
    }

    private fun updateInAir(delta: Float, map: GameMap) {
        verticalSpeed += gravity * delta
        moveVertically(delta, map)
    }

    private fun moveVertically(delta: Float, map: GameMap) {
        val totalStep = verticalSpeed * delta
        val steps = abs(totalStep).toInt().coerceAtLeast(1)
        val stepSize = totalStep / steps

        for (i in 0 until steps) {
            val newY = sprite.y + stepSize
            if (collidesAt(sprite.x, newY, map)) {
                if (verticalSpeed < 0f && !isInWater) isOnGround = true
                verticalSpeed = 0f
                return
            }
            sprite.y = newY
        }
        if (!isInWater) isOnGround = false
    }

    private fun collidesAt(x: Float, y: Float, map: GameMap): Boolean {
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

    fun dispose() {
        sprite.texture.dispose()
    }

    companion object {
        protected const val MAX_NAME_LENGTH = 20
        private const val WATER_BUOYANCY_COEFFICIENT = 1.4f // больше коэффициент - быстрее всплытие
        private const val WATER_DRAG_COEFFICIENT = 1f       // больше коэффициент - быстрее гасится скорость
        private const val WATER_MAX_VERTICAL_SPEED = 4f
        private const val WATER_GRAVITY_MULTIPLIER = 0.2f
    }
}
