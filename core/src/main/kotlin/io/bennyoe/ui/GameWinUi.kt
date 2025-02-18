package io.bennyoe.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.assets.TextureAsset
import io.bennyoe.screens.GameWinScreen
import ktx.actors.onChange
import ktx.actors.plusAssign
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d

class GameWinUi(
    assets: AssetStorage,
    gameWinScreen: GameWinScreen
) : WidgetGroup() {
    private val menuBgTexture = assets.get<Texture>(TextureAsset.MENU_BG.descriptor)
    private val menuBg = NinePatch(TextureRegion(menuBgTexture), 5, 5, 5, 5)

    init {
        val marginX = 100f
        val marginY = 100f
        val tableWidth = GAME_WIDTH - 2 * marginX
        val tableHeight = GAME_HEIGHT - 2 * marginY
        val skin = Skin(Gdx.files.internal("bearout.json"))

        val headline = scene2d.label(
            "Gut gemacht ... weiter spielen?",
            skin = Scene2DSkin.defaultSkin
        )

        val table = Table().apply {
            setBackground(NinePatchDrawable(menuBg))
            setSize(tableWidth, tableHeight) // Setzt eine feste Größe für das Menü
            setPosition(marginX, marginY)
        }

        table.add()
        table.add(headline).expandX()
            .padBottom(10f)
        table.add()

            .row()

        table.add()
        val innerTable = Table()
        table.add(innerTable).spaceTop(100f)
        var textButton = TextButton("Nein danke", skin, "secondary").apply {
            onChange { gameWinScreen.quitGame() }
        }
        innerTable.add(textButton).spaceTop(100f).expandX()

        textButton = TextButton("Ja, next level please", skin, "secondary").apply {
            onChange { gameWinScreen.continueGame() }
        }
        innerTable.add(textButton).spaceTop(50f).expandX().spaceLeft(50f)
        table.add()

        this += table
    }
}
