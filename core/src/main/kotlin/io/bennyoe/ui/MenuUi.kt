package io.bennyoe.ui

import BearoutMap
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.net.HttpRequestBuilder.json
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.assets.TextureAsset
import io.bennyoe.screens.MenuScreen
import ktx.actors.onChange
import ktx.actors.onClick
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
    private val menuBg = NinePatch(TextureRegion(menuBgTexture), 5, 5, 5, 5)

    init {
        setSize(GAME_WIDTH, GAME_HEIGHT)
        setPosition(0f, 0f)

        val table = Table().apply {
            setOrigin(Align.top)
            setSize(820f, 620f)
            setPosition(GAME_WIDTH / 2 - 410f, GAME_HEIGHT / 2 - 310f)
            setBackground(NinePatchDrawable(menuBg))
        }

        val headline = Label("Level Liste", customSkin).apply {
            setAlignment(Align.top)
        }

        val dropdown = SelectBox<BearoutMap>(customSkin).apply {
            items = getLevelInformation().toGdxArray()
            onChange {
                menuScreen.selectedLevel = selected
            }
        }

        val playTable = Table(customSkin).apply {}
        val playWithKeyboardButton = TextButton("Mit Keyboard spielen", customSkin).apply {
            setSize(500f, 50f)
            onClick { menuScreen.startGame(true) }
        }

        val playWithMouseButton = TextButton("Mit Maus spielen", customSkin).apply {
            setSize(500f, 50f)
            onClick { menuScreen.startGame(false) }
        }

        val highscoreButton = TextButton("Highscore", customSkin).apply {
            setSize(500f, 50f)
            onClick { menuScreen.setHighscoreScreen() }
        }

        val levelDesignerButton = TextButton("Level Designer", customSkin).apply {
            setSize(500f, 50f)
            onClick { menuScreen.setLevelDesignerScreen() }
        }

        playTable.add(playWithMouseButton)
            .padRight(30f)
        playTable.add(playWithKeyboardButton)
            .row()
        playTable.add(highscoreButton)
            .padTop(20f)
        playTable.add(levelDesignerButton)
            .padTop(20f)
            .padRight(30f)

        table.add(headline)
            .row()
        table.add(dropdown)
            .padTop(20f)
            .row()
        table.add(playTable)
            .padTop(20f)

//        table.debug = true
        this += table
    }

    private fun getLevelInformation(): List<BearoutMap> {
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
