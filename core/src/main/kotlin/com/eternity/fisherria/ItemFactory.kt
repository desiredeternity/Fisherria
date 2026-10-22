package com.eternity.fisherria

object ItemFactory {
    fun create(id: Int): Item {
        return when (id) {
            1 -> StartRod()
            2 -> AmateurRod()
            else -> StartRod()
        }
    }
}
