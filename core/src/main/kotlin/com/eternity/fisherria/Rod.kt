package com.eternity.fisherria

import com.badlogic.gdx.Gdx

abstract class Rod: Item() {
    abstract val addMastery: Float
    abstract val bobberTexturePath: String

    override fun use(context: ItemUseContext) {
        val player = context.player

        val bobber = Bobber(
            texturePath = bobberTexturePath,
            startX = player.getX() + player.width / 2f,
            startY = player.getY() + player.height * 0.7f,
            targetX = context.targetX,
            targetY = context.targetY
        )

        player.castBobber(bobber)
    }
}
