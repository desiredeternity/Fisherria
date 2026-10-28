package com.eternity.fisherria

class Player(x: Float, y: Float): Human() {
    override var name = "Игрок"
    override val texturePath = "player.png"
    override val height = 1f * 16/9
    override val width = 1f
    private var exp: Int = 0
    val inventory = Inventory(INVENTORY_CAPACITY)

    init {
        initSprite(x, y)
    }

    fun trySetName(name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.length > MAX_NAME_LENGTH) return false
        this.name = trimmed
        return true
    }

    fun toData(): PlayerData {
        return PlayerData(
            name = name,
            x = getX(),
            y = getY(),
            exp = exp,
            items = inventory.toData()
        )
    }

    fun applyData(data: PlayerData) {
        trySetName(data.name)
        setX(data.x)
        setY(data.y)
        exp = data.exp
        inventory.applyData(data.items)
    }

    companion object {
        private const val INVENTORY_CAPACITY = 20
    }
}
