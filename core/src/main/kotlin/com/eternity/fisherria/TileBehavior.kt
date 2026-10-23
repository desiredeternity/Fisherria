package com.eternity.fisherria

interface TileBehavior {
    val solid: Boolean
    val liquid: Boolean

    // множитель горизонтальной скорости
    fun getHorizontalSpeedMultiplier(entity: Entity): Float = 1f

    // множитель гравитации (1 - обычная, 0.2 вода и т.д.)
    fun getGravityMultiplier(entity: Entity): Float = 1f

    // максимальная вертикальная скорость (null - без ограничения)
    fun getMaxVerticalSpeed(entity: Entity): Float? = null

    fun getJumpForce(entity: Entity): Float? {
        return if (entity.isOnGround) entity.jumpPower else null
    }

    fun canBeOnGround(entity: Entity): Boolean = true

    fun onEntityStay(entity: Entity, delta: Float, map: GameMap, gravity: Float) {}
    fun onEntityEnter(entity: Entity) {}
    fun onEntityExit(entity: Entity) {}
}
