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

    // вызывается пока сущность находится на тайле
    fun onEntityStay(entity: Entity, delta: Float, map: GameMap, gravity: Float) {}

    // вызывается при входе на тайл
    fun onEntityEnter(entity: Entity) {}

    // вызывается при выходе с тайла
    fun onEntityExit(entity: Entity) {}
}
