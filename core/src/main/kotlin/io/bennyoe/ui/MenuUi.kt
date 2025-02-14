package io.bennyoe.ui

import BearoutMap
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.net.HttpRequestBuilder.json
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.screens.MenuScreen
import ktx.actors.plusAssign
import ktx.collections.toGdxArray
import ktx.log.logger

private val LOG = logger<MenuUi>()

class MenuUi(
    private val menuScreen: MenuScreen
) : WidgetGroup() {
    private val levelList = Gdx.files.local("levels").list()

    init {
        setSize(GAME_WIDTH, GAME_HEIGHT)
        setPosition(0f, 0f)

        val table = Table().apply {
            setFillParent(true)
            align(Align.top)
            padTop(50f)

        }

        val headline = Label("Level Liste", customSkin)
        val dropdown = SelectBox<BearoutMap>(customSkin).apply {
            items = getLevelInformation().toGdxArray()
        }

        dropdown.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                menuScreen.selectedLevel = dropdown.selected
            }
        })

//        val levelTable = Table(customSkin).apply {
//            width = 44f
//            height = 44f
//            background = createColorDrawable(0f,0f,0f,0.8f)
//        }
//
//        levelList.forEach { level ->
//            val map: BearoutMap = try {
//                json.fromJson(BearoutMap::class.java, level.readString())
//            } catch (e: Exception) {
//                Gdx.app.error("BrickSystem", "Fehler beim Laden der Level-Datei: ${e.localizedMessage}")
//                return@forEach
//            }
//            val levelText = "${map.name} von ${map.author} Schwierigkeit: ${map.difficulty}"
//            levelTable.add(Label(levelText, customSkin)).row()
//        }

        table.add(headline)
            .row()
        table.add(dropdown)

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
