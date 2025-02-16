package io.bennyoe.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.Main
import io.bennyoe.screens.GameOverScreen
import io.bennyoe.screens.LoadingScreen
import io.bennyoe.utillity.HighScoreManager
import io.bennyoe.utillity.PlayerHighscore
import ktx.actors.onKey
import ktx.actors.plusAssign
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d

class GameOverUi(
    private val score: Int,
    private val game: Main
) : WidgetGroup() {

    private val highScoreManager = HighScoreManager(game)
    private val highScores: List<PlayerHighscore> = highScoreManager.loadHighScores()

    init {
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
                    val playerName = text.trim()
                    if (playerName.isNotEmpty()) {
                        highScoreManager.saveHighScore(PlayerHighscore(playerName, score))
                    }
                    game.removeScreen<GameOverScreen>()
                    game.addScreen(LoadingScreen(game))
                    game.setScreen<LoadingScreen>()
                }
            }
        }

        // TODO show only if the score is higher or equal than the last in the highscore list
        val table = Table().apply {
            setFillParent(true)
            align(Align.top)
            padTop(50f)
        }

        val highscoreTable = Table().apply {
            align(Align.left)
        }

        highScores.forEachIndexed { index, playerHighscore ->
            val scoreText = "${index + 1}. ${playerHighscore.name} - ${playerHighscore.score}"
            highscoreTable.add(Label(scoreText, Scene2DSkin.defaultSkin).apply {
                setAlignment(Align.left)
            }).pad(5f).row()
        }

        table.add(showScore)
            .padBottom(10f)
            .row()

        table.add(nameInput)
            .width(300f)
            .height(40f)
            .padBottom(20f)
            .row()

        table.add(highscoreTable)
            .padBottom(20f)
            .row()

//        table.debug = true

        this += table
    }
}
