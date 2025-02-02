package io.bennyoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.bennyoe.Main
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.assets.SoundAsset
import ktx.assets.async.AssetStorage
import ktx.graphics.use
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table

class GameOverScreen(game: Main, val assets: AssetStorage = game.assets) : Screen(game) {
    private val bg = Texture("images/splash-screen.jpg")
    private lateinit var pressToBegin: Label

    init {
    audioService.play(SoundAsset.GAME_LOSE)
    }

    override fun show() {
        super.show()
        setupUserInterface()
    }

    private fun setupUserInterface() {
        stage.actors {
            table {
                defaults().fillX().expandX()
                label("Game Over", "default") {
                    setWrap(true)
                    setAlignment(Align.center)
                }
                row()
                defaults().fillX().expandX()
                pressToBegin = label("Press space to play with Keyboard", "default") {
                    setWrap(true)
                    setAlignment(Align.center)
                }
                row()
                pressToBegin = label("Click to play with mouse", "default") {
                    setWrap(true)
                    setAlignment(Align.center)
                }
                setFillParent(true)
                pack()
            }
        }
    }

    override fun hide() {
        stage.clear()
    }

    override fun render(delta: Float) {
        batch.use(viewport.camera.combined) {
            it.draw(bg, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
        }

            val isKeyboardControl = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            val isMouseControl = Gdx.input.justTouched()

            if (isKeyboardControl || isMouseControl) {
                game.removeScreen<GameOverScreen>()
                dispose()
                game.addScreen(GameScreen(game, assets, isKeyboardControl))
                game.setScreen<GameScreen>()
            }
        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }
}
