package com.eternity.fisherria

enum class TileType(val solid: Boolean, val liquid: Boolean = false) {
    EMPTY(false),
    WATER(false, true),
    GRASS(true),
    GROUND(true),
    ICE(true)
}
