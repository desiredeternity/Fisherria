package com.eternity.fisherria

import com.badlogic.gdx.graphics.g2d.SpriteBatch

abstract class Rod: Item() {
    abstract val addMastery: Float
    abstract val bobberTexturePath: String

    var activeBobber: Bobber? = null
        private set

    override fun use(context: ItemUseContext) {
        val current = activeBobber

        if (current != null) {
            // Летит — игнорируем
            if (current.isFlying) return

            // Плавает или лежит — подматываем / убираем
            current.dispose()
            activeBobber = null
            return
        }

        val player = context.player
        activeBobber = Bobber(
            texturePath = bobberTexturePath,
            startX = player.getX() + player.width / 2f,
            startY = player.getY() + player.height * 0.7f,
            targetX = context.targetX,
            targetY = context.targetY
        )
    }

    fun updateBobber(delta: Float, map: GameMap) {
        activeBobber?.update(delta, map)
    }

    fun drawBobber(batch: SpriteBatch) {
        activeBobber?.draw(batch)
    }

    fun clearBobber() {
        activeBobber?.dispose()
        activeBobber = null
    }

    override fun dispose() {
        clearBobber()
    }
}
