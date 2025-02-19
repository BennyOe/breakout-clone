package io.bennyoe.ui

import BearoutMap
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.net.HttpRequestBuilder.json
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.assets.TextureAsset
import io.bennyoe.screens.MenuScreen
import io.bennyoe.utillity.GameSettings
import ktx.actors.onChange
import ktx.actors.plusAssign
import ktx.assets.async.AssetStorage
import ktx.collections.toGdxArray
import ktx.log.logger

private val LOG = logger<MenuUi>()

class MenuUi(
    private val menuScreen: MenuScreen,
    assets: AssetStorage
) : WidgetGroup() {
    private val menuBgTexture = assets.get<Texture>(TextureAsset.MENU_BG.descriptor)
    private val levelList = Gdx.files.local("levels").list()
    private val bearoutLevel =  json.fromJson(BearoutMap::class.java, Gdx.files.internal("levels/Bearout.json"))
    private val jojoLevel =  json.fromJson(BearoutMap::class.java, Gdx.files.internal("levels/Jojo.json"))
    private val miraLevel =  json.fromJson(BearoutMap::class.java, Gdx.files.internal("levels/Mira.json"))
    private val menuBg = NinePatch(TextureRegion(menuBgTexture), 5, 5, 5, 5)

    init {
        val marginX = 100f
        val marginY = 100f
        val tableWidth = GAME_WIDTH - 2 * marginX
        val tableHeight = GAME_HEIGHT - 2 * marginY

        val skin = Skin(Gdx.files.internal("bearout.json"))
        val table = Table().apply {
            setBackground(NinePatchDrawable(menuBg))
            setSize(tableWidth, tableHeight) // Setzt eine feste Größe für das Menü
            setPosition(marginX, marginY)
        }

        table.add().expandY()

        var label = if (menuScreen.hasAlreadyPlayed) Label("Deine Punkte ${menuScreen.score}", skin) else Label("Bearout", skin)
        table.add(label)

        table.add()

        table.row()
        table.add()

        label = if (menuScreen.hasAlreadyPlayed) Label("Naechstes Level", skin) else Label("Level Liste", skin, "secondary")
        table.add(label).spaceBottom(10f)

        table.add()

        table.row()
        table.add()

        val selectBox = SelectBox<BearoutMap>(skin).apply {
            items = (getLevelInformation(levelList) + bearoutLevel + jojoLevel + miraLevel).toGdxArray()
            selected = items.firstOrNull { it.name == "Bearout" }
            setAlignment(Align.center)
            onChange {
                menuScreen.selectedLevel = selected
            }
        }
        table.add(selectBox).fillX()

        table.add()

        table.row()
        val playWithMouse = CheckBox(" Maus", skin).apply {
            onChange { GameSettings.playWithKeyboard = false }
        }
        val playWithKeyboard = CheckBox(" Tastatur", skin).apply {
            onChange { GameSettings.playWithKeyboard = true }
        }
        ButtonGroup<CheckBox>().apply {
            add(playWithMouse, playWithKeyboard)
            setMinCheckCount(1)
            setMaxCheckCount(1)
        }
        table.add(playWithMouse).spaceTop(50.0f).spaceBottom(10.0f)

        table.add()

        table.add(playWithKeyboard).spaceTop(50.0f).spaceBottom(10.0f)

        table.row()
        var textButton = TextButton("Highscores", skin, "secondary").apply {
            onChange { menuScreen.setHighscoreScreen() }
        }
        if (!menuScreen.hasAlreadyPlayed) table.add(textButton).spaceTop(50f)

        table.add()

        textButton = TextButton("Level Designer", skin, "secondary").apply {
            onChange { menuScreen.setLevelDesignerScreen() }
        }
        if (!menuScreen.hasAlreadyPlayed) table.add(textButton).spaceTop(50f)

        table.row()
        table.add().expandY()

        textButton = TextButton("PLAY", skin).apply {
            onChange { menuScreen.startGame() }
        }
        table.add(textButton).spaceTop(10.0f).fillX()

        table.add()
        this += table
    }

    private fun getLevelInformation(levelList: Array<FileHandle>): List<BearoutMap> {
        val mapList = mutableListOf<BearoutMap>()
        levelList.forEach { level ->
            mapList.add(
                try {
                    json.fromJson(BearoutMap::class.java, level.readString())
                } catch (e: Exception) {
                    Gdx.app.error("BrickSystem", "Fehler beim Laden der Level-Datei: ${e.localizedMessage}")
                    return@forEach
                }
            )
        }
        return mapList
    }
}
