package com.eternity.fisherria

import com.eternity.fisherria.Entity.Companion.WATER_BUOYANCY_COEFFICIENT
import com.eternity.fisherria.Entity.Companion.WATER_DRAG_COEFFICIENT
import com.eternity.fisherria.Entity.Companion.WATER_GRAVITY_MULTIPLIER
import com.eternity.fisherria.Entity.Companion.WATER_MAX_VERTICAL_SPEED

object EmptyBehavior: TileBehavior {
    override val solid = false
    override val liquid = false

    override fun getJumpForce(entity: Entity): Float? = null
}

object GrassBehavior: TileBehavior {
    override val solid = true
    override val liquid = false
}

object GroundBehavior: TileBehavior {
    override val solid = true
    override val liquid = false
}

object WaterBehavior: TileBehavior {
    override val solid = false
    override val liquid = true

    override fun getHorizontalSpeedMultiplier(entity: Entity) = 0.5f
    override fun getGravityMultiplier(entity: Entity) = WATER_GRAVITY_MULTIPLIER
    override fun getMaxVerticalSpeed(entity: Entity) = WATER_MAX_VERTICAL_SPEED

    override fun getJumpForce(entity: Entity): Float {
        return entity.jumpPower * 0.8f
    }

    override fun canBeOnGround(entity: Entity) = false

    override fun onEntityStay(entity: Entity, delta: Float, map: GameMap, gravity: Float) {
        val waterGravity = gravity * WATER_GRAVITY_MULTIPLIER
        val surfaceY = map.getLiquidLevelAt(entity.getX() + entity.width / 2f) ?: return

        val submergedDepth = surfaceY - entity.getY()
        if (submergedDepth > 0f) {
            // постоянное выталкивание
            entity.addVerticalForce(-waterGravity * WATER_BUOYANCY_COEFFICIENT * delta)

            // сопротивление воды
            val drag = 1f - WATER_DRAG_COEFFICIENT * delta
            entity.multiplyVerticalSpeed(drag.coerceAtLeast(0f))
        }
    }
}

object IceBehavior: TileBehavior {
    override val solid = true
    override val liquid = false

    override fun getHorizontalSpeedMultiplier(entity: Entity) = 2.5f
}

object TileBehaviors {
    fun of(type: TileType): TileBehavior {
        return when (type) {
            TileType.EMPTY -> EmptyBehavior
            TileType.GRASS -> GrassBehavior
            TileType.GROUND -> GroundBehavior
            TileType.WATER -> WaterBehavior
            TileType.ICE -> IceBehavior
        }
    }
}
