package io.bennyoe.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Json
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.Main
import io.bennyoe.screens.GameOverScreen
import io.bennyoe.screens.LoadingScreen
import ktx.actors.plusAssign
import ktx.preferences.flush
import ktx.preferences.set
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d

class GameOverUi(
    private val score: Int,
    private val game: Main
) : WidgetGroup() {

    private val highScores: List<PlayerHighscore> = loadHighScores()

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
        }
        nameInput.setTextFieldListener { _, key ->
            if (key == '\n' || key == '\r') { // Wenn Enter gedrückt wird
                val playerName = nameInput.text.trim()
                if (playerName.isNotEmpty()) {
                    saveHighScore(PlayerHighscore(playerName, score))
                    game.removeScreen<GameOverScreen>()
                    game.addScreen(LoadingScreen(game))
                    game.setScreen<LoadingScreen>()
                }
            }
        }

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

    private fun loadHighScores(): MutableList<PlayerHighscore> {
        val json = Json()
        val storedScores = game.preferences.getString("highscore", "[]")

        val highscoreList: MutableList<PlayerHighscore> = try {
            json.fromJson(Array<PlayerHighscore>::class.java, storedScores).toMutableList()
        } catch (e: Exception) {
            println("Fehler beim Laden der Highscores: ${e.message}")
            mutableListOf()
        }

        return highscoreList.sortedByDescending { it.score }.take(10).toMutableList()
    }

    private fun saveHighScore(playerHighscore: PlayerHighscore) {
        val json = Json()
        val prevHighscore = loadHighScores()
        prevHighscore.add(playerHighscore)

        game.preferences.flush {
            this["highscore"] = json.toJson(prevHighscore)
        }
    }
}

data class PlayerHighscore(
    val name: String = "",
    val score: Int = 0
)
