package com.eternity.fisherria

object EmptyBehavior: TileBehavior {
    override val solid = false
    override val liquid = false
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
}

object IceBehavior: TileBehavior {
    override val solid = true
    override val liquid = false
    override val horizontalSpeedMultiplier = 2.5f
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
