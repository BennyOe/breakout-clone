package io.bennyoe.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.Main
import io.bennyoe.utillity.HighScoreManager
import io.bennyoe.utillity.PlayerHighscore
import ktx.actors.plusAssign
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d

class GameOverUi(
    game: Main
) : WidgetGroup() {

    private val highScoreManager = HighScoreManager(game)
    private val highScores: List<PlayerHighscore> = highScoreManager.loadHighScores()

    init {
        setSize(GAME_WIDTH, GAME_HEIGHT)
        setPosition(0f, 0f)

        val headline = scene2d.label(
            "HIGHSCORES",
            skin = Scene2DSkin.defaultSkin
        )

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

        table.add(headline)
            .padBottom(10f)
            .row()

        table.add(highscoreTable)
            .padBottom(20f)
            .row()

        this += table
    }
}
