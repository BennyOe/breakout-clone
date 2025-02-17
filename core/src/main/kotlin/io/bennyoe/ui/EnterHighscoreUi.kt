package io.bennyoe.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.Main
import io.bennyoe.screens.GameOverScreen
import io.bennyoe.utillity.HighScoreManager
import io.bennyoe.utillity.PlayerHighscore
import ktx.actors.onChange
import ktx.actors.onKey
import ktx.actors.plusAssign
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d

class EnterHighscoreUi(
    private val gameOverScreen: GameOverScreen,
    private val score: Int,
    game: Main
) : WidgetGroup() {

    private val highScoreManager = HighScoreManager(game)

    init {
        val skin = Skin(Gdx.files.internal("bearout.json"))
        setSize(GAME_WIDTH, GAME_HEIGHT)
        setPosition(0f, 0f)

        val showScore = scene2d.label(
            "Deine Punkte: $score",
            skin = Scene2DSkin.defaultSkin
        ).apply {
        }

        val nameInput = TextField("", Scene2DSkin.defaultSkin).apply {
            messageText = "Enter name"
            onKey { key ->
                if (key == '\n' || key == '\r') { // Wenn Enter gedrückt wird
                    saveHighScore(this.text)
                }
            }
        }

        val textButton = TextButton("Speichern", skin, "secondary").apply {
            onChange {
                saveHighScore(nameInput.text)
            }
        }

        val table = Table().apply {
            setFillParent(true)
            align(Align.top)
            padTop(50f)
        }

        table.add(showScore)
            .padBottom(10f)
            .row()

        table.add(nameInput)
            .width(300f)
            .height(40f)
            .padBottom(20f)
            .row()

        table.add(textButton)

        this += table
    }

    private fun saveHighScore(playerName: String) {
        if (playerName.trim().isNotEmpty()) {
            highScoreManager.saveHighScore(PlayerHighscore(playerName, score))
        }
        gameOverScreen.showHighScores()
    }
}
