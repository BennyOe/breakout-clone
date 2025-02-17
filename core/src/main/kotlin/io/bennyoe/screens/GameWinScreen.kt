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

class GameWinScreen(
    game: Main,
    val assets: AssetStorage = game.assets,
    val score: Int
) : Screen(game) {
    private val bg = Texture("images/win.jpg")
    private lateinit var pressToBegin: Label

    init {
        audioService.play(SoundAsset.GAME_WIN)
    }

    override fun show() {
        super.show()
        setupUserInterface()
    }

    private fun setupUserInterface() {
        stage.actors {
            table {
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            game.removeScreen<GameWinScreen>()
            dispose()
            game.addScreen(MenuScreen(game, score, assets))
            game.setScreen<MenuScreen>()
        }

        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }
}
