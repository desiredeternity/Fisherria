package com.eternity.fisherria

import com.badlogic.gdx.math.MathUtils

abstract class Entity: PhysicsObject() {
    abstract var name: String
        protected set

    protected open val horizontalSpeed: Float = 8f
    open val jumpPower: Float = 12f

    var isOnGround: Boolean = true
        private set

    private var facingRight: Boolean = false

    fun move(direction: Direction, delta: Float, worldWidth: Float, map: GameMap) {
        val behavior = map.getBehaviorAt(sprite.x + width / 2f, sprite.y + height / 2f)
        val speedMultiplier = behavior.getHorizontalSpeedMultiplier(this)

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

    fun jump(map: GameMap) {
        // Смотрим тайл прямо под ногами
        val feetBehavior = map.getBehaviorAt(
            sprite.x + width / 2f,
            sprite.y - 0.05f
        )

        val force = feetBehavior.getJumpForce(this) ?: return

        verticalSpeed = force
        setJustJumped(feetBehavior.liquid)
        isOnGround = false
    }

    override fun onAfterVerticalMove(map: GameMap) {
        val feetY = sprite.y - 0.1f
        val onSolid = collidesAt(sprite.x, feetY, map)
        isOnGround = onSolid && currentBehavior.canBeOnGround(this)
    }

    companion object {
        protected const val MAX_NAME_LENGTH = 20
        const val WATER_BUOYANCY_COEFFICIENT = 1.4f // больше коэффициент - быстрее всплытие
        const val WATER_DRAG_COEFFICIENT = 1f       // больше коэффициент - быстрее гасится скорость
        const val WATER_MAX_VERTICAL_SPEED = 4f
        const val WATER_GRAVITY_MULTIPLIER = 0.2f
    }
}
