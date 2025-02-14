package io.bennyoe.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.screens.LevelDesignerScreen
import ktx.actors.plusAssign


class LevelDesignerUi(leveldesigner: LevelDesignerScreen) : WidgetGroup() {
    init {
        setSize(GAME_WIDTH, GAME_HEIGHT)
        setPosition(0f, 0f)

        val nameInput = TextField("", customSkin).apply {
            messageText = "Level Name"
            alignment = Align.center

            setTextFieldListener { _, key ->
                leveldesigner.bearoutMap.name = this.text
                if (key == '\n' || key == '\r') { // Wenn Enter gedrückt wird
                    leveldesigner.saveMap()
                }
            }
        }


        val authorInput = TextField("", customSkin).apply {
            messageText = "Level Autor"
            alignment = Align.center
            setTextFieldListener { _, key ->
                leveldesigner.bearoutMap.author = this.text
            }
        }

        val difficultyLabel = Label("Schwierigkeit", customSkin)
        val difficulty = Slider(1f, 5f, 1f, false, customSkin).apply {
            value = 3f
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    leveldesigner.bearoutMap.difficulty = value.toInt()
                }
            })
        }

        val saveButton = TextButton("Save", customSkin, "saveButton").apply {
            addListener(
                object : ClickListener() {
                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        if (nameInput.text.isNotEmpty()) {
                            leveldesigner.saveMap()
                        }
                    }
                })
        }

        val table = Table().apply {
            setFillParent(true)
            align(Align.bottomRight)
            padRight(20f)
            add(difficultyLabel)
                .row()
            add(difficulty)
                .width(450f)
                .height(40f)
                .row()
            add(authorInput)
                .width(450f)
                .height(40f)
                .padBottom(20f)
                .row()
            add(nameInput)
                .width(450f)
                .height(40f)
                .padBottom(20f)
            add(saveButton)
                .width(100f)
                .height(40f)
                .padLeft(60f)
                .padBottom(20f)
        }

        this += table
    }
}
