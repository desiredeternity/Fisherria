package com.eternity.fisherria

class StartRod: Rod() {
    override val id = 1
    override val name = "Удочка новичка"
    override val description = "Это твоя первая удочка!"
    override val iconPath = "start_rod.png"
    override val bobberTexturePath = "start_bobber.png"
    override val addMastery = 0f
}
