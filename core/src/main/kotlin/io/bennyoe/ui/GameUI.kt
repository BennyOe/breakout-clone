package io.bennyoe.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import ktx.actors.plusAssign
import ktx.log.logger
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d

private val LOG = logger<GameUI>()

class GameUI(
) : WidgetGroup() {  // Group → WidgetGroup für besseres Layout
    private val hearts = mutableListOf<Image>()
    private val emptyHearts = mutableListOf<Image>()
    private var displayScore = scene2d.label("Punkte: 0") {
        setFontScale(0.5f)
        setPosition(10f, 0f)
    }

    init {

        setSize(GAME_WIDTH, GAME_HEIGHT)
        setPosition(0f, 0f)

        this += displayScore

        repeat(5) { index ->
            val heart = scene2d.image("heart").apply {
                setPosition((GAME_WIDTH - 170) + index * 32f, 0f)
                setSize(32f, 32f)
            }
            val emptyHeart = scene2d.image("heart_empty").apply {
                setPosition((GAME_WIDTH - 170) + index * 32f, 0f)
                setSize(32f, 32f)
                isVisible = false
            }
            hearts.add(heart)
            this += heart

            emptyHearts.add(emptyHeart)
            this += emptyHeart
        }
    }

    fun refreshHearts(lives: Int) {
        hearts.forEachIndexed { index, heart ->
            heart.isVisible = index < lives
        }
        emptyHearts.forEachIndexed { index, heart ->
            heart.isVisible = index >= lives
        }
    }

    fun refreshScore(score: Int) {
        Gdx.app.postRunnable {
            displayScore.setText("Punkte: $score")
        }
    }
}
