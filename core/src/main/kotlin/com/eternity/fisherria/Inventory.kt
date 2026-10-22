package com.eternity.fisherria

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class Inventory(capacity: Int) {
    private val slots: Array<Item?> = arrayOfNulls(capacity)
    private val iconCache = mutableMapOf<Int, Texture>() // кэширование через хэш-таблицу
    private val slotSize: Float = 1f
    private val slotPadding: Float = 0.1f
    private val slotTexture: Texture = Texture("slot.png")
    private val currentSlotTexture: Texture = Texture("slot_selected.png")
    var currentSlot: Int = 0
        private set

    fun selectNextSlot() {
        currentSlot = (currentSlot + 1) % VISIBLE_SLOTS
    }

    fun selectPreviousSlot() {
        currentSlot = (currentSlot - 1 + VISIBLE_SLOTS) % VISIBLE_SLOTS
    }

    fun selectSlot(index: Int) {
        if (index in 0 until VISIBLE_SLOTS) {
            currentSlot = index
        }
    }

    fun getCurrentItem(): Item? {
        return slots.getOrNull(currentSlot)
    }

    fun addItem(item: Item): Boolean {
        for (slot in slots) { // пытаемся положить в существующий стак
            if (slot != null && slot.canStackWith(item)) {
                val leftover = slot.addCount(item.count)
                if (leftover == 0) return true
                item.setCount(leftover)
            }
        }

        for (i in slots.indices) { // ищем пустой слот
            if (slots[i] == null) {
                slots[i] = item
                return true
            }
        }

        return false // инвентарь полон
    }

    fun removeItem(item: Item, amount: Int = 1): Boolean {
        var remaining = amount
        for (i in slots.indices) {
            val slot = slots[i] ?: continue
            if (slot.id != item.id) continue
            val toRemove = minOf(remaining, slot.count)
            slot.removeCount(toRemove)
            remaining -= toRemove
            if (slot.isEmpty()) slots[i] = null
            if (remaining <= 0) return true
        }
        return false
    }

    fun isFull(): Boolean {
        return slots.none { it == null }
    }

    fun getItem(index: Int): Item? {
        return slots.getOrNull(index)
    }

    fun clear() {
        for (i in slots.indices) {
            slots[i] = null
        }
    }

    fun getAllItems(): List<Item> {
        return slots.filterNotNull()
    }

    fun getVisibleItems(): List<Item?> {
        return slots.take(VISIBLE_SLOTS).toList()
    }

    fun toData(): MutableList<ItemData> {
        return getAllItems().map { ItemData(it.id, it.count) }.toMutableList()
    }

    fun applyData(data: List<ItemData>) {
        clear()

        for (itemData in data) {
            val item = ItemFactory.create(itemData.id)
            item.setCount(itemData.count)
            addItem(item)
        }
    }

    private fun getIcon(item: Item): Texture {
        var texture = iconCache[item.id]
        if (texture == null) {
            texture = Texture(item.iconPath)
            iconCache[item.id] = texture
        }
        return texture
    }

    fun drawFull(batch: SpriteBatch, font: BitmapFont, x: Float, y: Float) {
        for ((index, item) in getAllItems().withIndex()) {
            val slotX = x + index * (slotSize + slotPadding)
            val icon = getIcon(item)
            batch.draw(icon, slotX, y, slotSize, slotSize)

            if (item.count > 0) {
                font.draw(batch, item.count.toString(), slotX + 0.6f, y + 0.4f)
            }
        }
    }

    fun drawVisible(batch: SpriteBatch, font: BitmapFont, x: Float, y: Float) {
        val visibleItems = getVisibleItems()
        for (i in 0 until VISIBLE_SLOTS) {
            val slotX = x + i * (slotSize + slotPadding)

            val isSelected = (i == currentSlot)
            if (isSelected) batch.draw(currentSlotTexture, slotX, y, slotSize, slotSize)
            else batch.draw(slotTexture, slotX, y, slotSize, slotSize)

            val item = visibleItems.getOrNull(i) ?: continue
            val icon = getIcon(item)
            batch.draw(icon, slotX, y, slotSize, slotSize)

            if (item.count > 1) {
                font.draw(batch, item.count.toString(), slotX + 0.6f, y + 0.4f)
            }
        }
    }

    fun dispose() {
        iconCache.values.forEach { it.dispose() }
        iconCache.clear()
        slotTexture.dispose()
        currentSlotTexture.dispose()
    }

    companion object {
        private const val VISIBLE_SLOTS = 5
    }
}
