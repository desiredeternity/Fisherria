package com.eternity.fisherria

abstract class Item {
    abstract val id: Int
    abstract val name: String
    abstract val iconPath: String
    open val description: String = ""
    open val rarity: Rarity = Rarity.COMMON
    open val buyPrice: Int = 0
    open val sellPrice: Int = 0
    open val dropChance: Float = 0f
    open val gainedExp: Float = 0f
    open val maxStack: Int = 1

    var count: Int = 1
        private set

    abstract fun use(context: ItemUseContext)

    fun setCount(value: Int) {
        count = value.coerceAtLeast(0) // значение не уйдёт ниже 0
    }

    fun canStackWith(other: Item): Boolean {
        return this.id == other.id && count < maxStack
    }

    fun addCount(amount: Int): Int {
        val space = maxStack - count
        val toAdd = minOf(amount, space)
        count += toAdd
        return amount - toAdd
    }

    fun removeCount(amount: Int): Boolean {
        if (amount > count) return false
        count -= amount
        return true
    }

    fun isEmpty(): Boolean {
        return count <= 0
    }
}
