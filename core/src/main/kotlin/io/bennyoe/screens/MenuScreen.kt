package io.bennyoe.screens

import BearoutMap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.Main
import io.bennyoe.ui.MenuUi
import ktx.assets.async.AssetStorage
import ktx.log.logger

private val LOG = logger<MenuScreen>()

class MenuScreen(
    game: Main,
    val score: Int = 0,
    val hasAlreadyPlayed: Boolean = false,
    val assets: AssetStorage = game.assets
) : Screen(game) {
    private val bg = Texture("images/splash-screen.jpg")
    private val ui by lazy { MenuUi(this, assets) }
    var selectedLevel: BearoutMap? = null

    override fun show() {
        audioService.resume()
        setupUserInterface()
        super.show()
    }

    private fun setupUserInterface() {
        val backgroundImage = Image(TextureRegionDrawable(TextureRegion(bg)))
        backgroundImage.setSize(GAME_WIDTH, GAME_HEIGHT)
        backgroundImage.setPosition(0f, 0f)
        stage.addActor(backgroundImage)
        stage.addActor(ui)
    }

    override fun hide() {
        stage.clear()
    }

    override fun render(delta: Float) {
        stage.run {
            viewport.apply()
            stage.act()
            stage.draw()
        }
    }

    fun startGame() {
        game.removeScreen<MenuScreen>()
        game.addScreen(GameScreen(game, assets, selectedLevel, score = score))
        game.setScreen<GameScreen>()
    }

    fun setHighscoreScreen() {
        game.removeScreen<MenuScreen>()
        game.addScreen(GameOverScreen(game, 0, true))
        game.setScreen<GameOverScreen>()
    }

    fun setLevelDesignerScreen() {
        game.removeScreen<MenuScreen>()
        game.addScreen(LevelDesignerScreen(game, assets))
        game.setScreen<LevelDesignerScreen>()
    }
}
