package com.eternity.fisherria

class AmateurRod: Rod() {
    override val id = 2
    override val name = "Удочка любителя"
    override val description = "Это твоя вторая удочка!"
    override val iconPath = "amateur_rod.png"
    override val bobberTexturePath = "start_bobber.png"

    override val addMastery = 5f
}
