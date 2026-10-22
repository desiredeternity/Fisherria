package com.eternity.fisherria

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.JsonWriter.OutputType

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Main : ApplicationAdapter() {
    private lateinit var spriteBatch: SpriteBatch // инструмент для отрисовки графики. объединяет все вызовы отрисовки
    private lateinit var viewport: FitViewport    // тип окна просмотра
    private lateinit var uiViewport: FitViewport
    private lateinit var uiCamera: OrthographicCamera
    private lateinit var touchPos: Vector2
    private lateinit var font: BitmapFont

    private lateinit var map: GameMap
    private lateinit var player: Player
    private lateinit var slotTexture: Texture
    private lateinit var backgroundTexture: Texture

    private val json = Json().apply {
        setOutputType(OutputType.json)
    }

    override fun create() { // выполняется во время запуска программы
        spriteBatch = SpriteBatch()
        viewport = FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
        viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
        uiViewport = FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
        uiViewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
        uiCamera = uiViewport.camera as OrthographicCamera
        touchPos = Vector2()
        font = BitmapFont()
        font.data.setScale(0.05f)

        map = GameMap(MapLoader.loadFromPng("map.png"))
        map.loadTexture()
        player = createPlayer()

        slotTexture = Texture("slot_selected.png")
        backgroundTexture = Texture("background.png")
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        input()
        player.update(delta, map)
        logic()
        draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }

    override fun pause() {
        savePlayer(player)
    }

    override fun dispose() {
        savePlayer(player)
        spriteBatch.dispose()
        player.inventory.dispose()
        player.dispose()
        backgroundTexture.dispose()
        font.dispose()
        map.dispose()
    }

    private fun input() {
        val delta: Float = Gdx.graphics.deltaTime

        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.move(Direction.LEFT, delta, map.width, map)
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.move(Direction.RIGHT, delta, map.width, map)
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) player.jump()
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) player.inventory.selectSlot(0)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) player.inventory.selectSlot(1)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) player.inventory.selectSlot(2)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) player.inventory.selectSlot(3)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) player.inventory.selectSlot(4)

        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat()) // узнать где произошёл клик по экрану
            viewport.unproject(touchPos) // конвертировать координаты окна в координаты мира

            val context = ItemUseContext(
                touchPos.x,
                touchPos.y,
                map
            )
            // player.inventory.getCurrentItem()?.use(context) // если метод вернёт null, ничего не произойдёт (оператор ?.)
        }
    }

    private fun logic() {
        viewport.camera.position.set(player.getX(), player.getY(), 0f)
        viewport.camera.update()
    }

    private fun draw() {
        ScreenUtils.clear(Color.BLACK) // очистка экрана

        // мир
        viewport.apply()
        spriteBatch.setProjectionMatrix(viewport.camera.combined)
        spriteBatch.begin()
        spriteBatch.draw(backgroundTexture, 0f, 0f, map.width, map.height)
        player.draw(spriteBatch)
        map.draw(spriteBatch)
        spriteBatch.end()

        // ui
        uiViewport.apply()
        spriteBatch.setProjectionMatrix(uiViewport.camera.combined)
        spriteBatch.begin()
        player.inventory.drawVisible(spriteBatch, font, 0.25f, VIEWPORT_HEIGHT - 1.25f)
        spriteBatch.end()
    }

    private fun createPlayer(): Player {
        val data = loadPlayer()
        return if (data != null) {
            val p = Player(data.x, data.y)
            p.applyData(data)
            p
        } else {
            val p = Player(map.width / 2f, 20f)
            p.inventory.addItem(ItemFactory.create(1))
            p
        }
    }

    private fun savePlayer(player: Player) {
        val file: FileHandle = Gdx.files.local("player.json")
        try {
            val data = player.toData()
            val jsonString = json.prettyPrint(data)
            file.writeString(jsonString, false, "UTF-8")
            Gdx.app.log("Save", "Player saved: ${file.path()}")
        } catch (e: Exception) {
            Gdx.app.error("Save", "Failed to save player: ${e.message}")
        }
    }

    private fun loadPlayer(): PlayerData? {
        val file: FileHandle = Gdx.files.local("player.json")

        if (!file.exists()) {
            Gdx.app.log("Load", "No player file found")
            return null
        }

        return try {
            val jsonString = file.readString("UTF-8")
            val data = json.fromJson(PlayerData::class.java, jsonString)
            Gdx.app.log("Load", "Player loaded")
            data
        } catch (e: Exception) {
            Gdx.app.error("Load", "Failed to load player: ${e.message}")
            null
        }
    }

    companion object {
        private const val VIEWPORT_WIDTH: Float = 30f
        private const val VIEWPORT_HEIGHT: Float = 15f
    }
}
