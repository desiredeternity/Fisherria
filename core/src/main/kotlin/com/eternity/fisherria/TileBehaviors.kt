package com.eternity.fisherria

import com.eternity.fisherria.PhysicsObject.Companion.WATER_BUOYANCY_COEFFICIENT
import com.eternity.fisherria.PhysicsObject.Companion.WATER_DRAG_COEFFICIENT
import com.eternity.fisherria.PhysicsObject.Companion.WATER_GRAVITY_MULTIPLIER
import com.eternity.fisherria.PhysicsObject.Companion.WATER_MAX_VERTICAL_SPEED

object EmptyBehavior: TileBehavior {
    override val solid = false
    override val liquid = false

    override fun getJumpForce(obj: PhysicsObject): Float? = null
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

    override fun getHorizontalSpeedMultiplier(obj: PhysicsObject) = 0.5f
    override fun getGravityMultiplier(obj: PhysicsObject) = WATER_GRAVITY_MULTIPLIER
    override fun getMaxVerticalSpeed(obj: PhysicsObject) = WATER_MAX_VERTICAL_SPEED

    override fun getJumpForce(obj: PhysicsObject): Float? {
        return if (obj is Entity) obj.jumpPower * 0.8f else null
    }

    override fun canBeOnGround(obj: PhysicsObject) = false

    override fun onEntityStay(obj: PhysicsObject, delta: Float, map: GameMap, gravity: Float) {
        val waterGravity = gravity * WATER_GRAVITY_MULTIPLIER
        val surfaceY = map.getLiquidLevelAt(obj.getX() + obj.width / 2f) ?: return

        val submergedDepth = surfaceY - obj.getY()
        if (submergedDepth > 0f) {
            // постоянное выталкивание
            obj.addVerticalForce(-waterGravity * WATER_BUOYANCY_COEFFICIENT * delta)

            // сопротивление воды
            val drag = 1f - WATER_DRAG_COEFFICIENT * delta
            obj.multiplyVerticalSpeed(drag.coerceAtLeast(0f))
        }
    }
}

object IceBehavior: TileBehavior {
    override val solid = true
    override val liquid = false

    override fun getHorizontalSpeedMultiplier(obj: PhysicsObject) = 2.5f
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
