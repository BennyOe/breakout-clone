package io.bennyoe.ui

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import ktx.scene2d.image
import ktx.scene2d.scene2d
import ktx.actors.plusAssign
import ktx.log.logger

private val LOG = logger<GameUI>()
class GameUI : Group() {
    private val hearts = mutableListOf<Image>()
    private val emptyHearts = mutableListOf<Image>()

    init {
        repeat(5) { index ->
            val heart = scene2d.image("heart").apply {
                setPosition(index * 32f, 0f)
                setSize(32f, 32f)
            }
            val emptyHeart = scene2d.image("heart_empty").apply {
                setPosition(index * 32f, 0f)
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
}
