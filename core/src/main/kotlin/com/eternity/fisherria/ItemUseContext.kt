package com.eternity.fisherria

data class ItemUseContext(
    val player: Player,
    val targetX: Float,
    val targetY: Float,
    val map: GameMap,
)
