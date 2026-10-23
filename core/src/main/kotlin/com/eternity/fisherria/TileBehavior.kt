package com.eternity.fisherria

interface TileBehavior {
    val solid: Boolean
    val liquid: Boolean

    // множитель горизонтальной скорости
    fun getHorizontalSpeedMultiplier(obj: PhysicsObject): Float = 1f

    // множитель гравитации (1 - обычная, 0.2 вода и т.д.)
    fun getGravityMultiplier(obj: PhysicsObject): Float = 1f

    // максимальная вертикальная скорость (null - без ограничения)
    fun getMaxVerticalSpeed(obj: PhysicsObject): Float? = null

    fun getJumpForce(obj: PhysicsObject): Float? {
        return if (obj is Entity && obj.isOnGround) obj.jumpPower else null
    }
    fun canBeOnGround(obj: PhysicsObject): Boolean = true

    fun onEntityStay(obj: PhysicsObject, delta: Float, map: GameMap, gravity: Float) {}
    fun onEntityEnter(obj: PhysicsObject) {}
    fun onEntityExit(obj: PhysicsObject) {}
}
