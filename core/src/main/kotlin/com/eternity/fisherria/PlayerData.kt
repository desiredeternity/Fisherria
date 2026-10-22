package com.eternity.fisherria

data class PlayerData (
    var name: String = "None",
    var x: Float = 0f,
    var y: Float = 0f,
    var exp: Int = 0,
    var items: MutableList<ItemData> = mutableListOf()
)
