package com.eternity.fisherria

import com.badlogic.gdx.Gdx

abstract class Rod: Item() {
    abstract val addMastery: Float
    abstract val bobberTexturePath: String

    override fun use(context: ItemUseContext) {
        Gdx.app.log("Rod", "Rod used")
        val bobber = Bobber(
            texturePath = bobberTexturePath,
            startX = context.targetX,
            startY = context.targetY,
            targetX = context.targetX,
            targetY = context.targetY
        )
    }


}
