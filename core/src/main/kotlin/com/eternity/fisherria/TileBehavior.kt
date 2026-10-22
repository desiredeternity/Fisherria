package com.eternity.fisherria

interface TileBehavior {
    val solid: Boolean
    val liquid: Boolean
    val horizontalSpeedMultiplier: Float get() = 1f
}
