package io.bennyoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Label
import io.bennyoe.Main
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.assets.SoundAsset
import io.bennyoe.ui.GameOverUi
import ktx.graphics.use

class GameOverScreen(
    game: Main,
    private val score: Int
) : Screen(game) {
    private val bg = Texture("images/gameover.jpg")
    private lateinit var pressToBegin: Label
    private val ui by lazy { GameOverUi(score, game) }

    init {
        audioService.play(SoundAsset.GAME_LOSE)
    }

    override fun show() {
        super.show()
        setupUserInterface()
    }

    private fun setupUserInterface() {
        stage.addActor(ui)
    }

    override fun hide() {
        stage.clear()
    }

    override fun render(delta: Float) {
        batch.use(viewport.camera.combined) {
            it.draw(bg, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
        }

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.removeScreen<GameOverScreen>()
            game.addScreen(LoadingScreen(game))
            game.setScreen<LoadingScreen>()
        }
//            val isKeyboardControl = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
//            val isMouseControl = Gdx.input.justTouched()
//
//            if (isKeyboardControl || isMouseControl) {
//                game.removeScreen<GameOverScreen>()
//                dispose()
//                game.addScreen(GameScreen(game, assets, isKeyboardControl))
//                game.setScreen<GameScreen>()
//            }
        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }
}
