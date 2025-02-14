package io.bennyoe.screens

import BearoutMap
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import io.bennyoe.Main
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.ecs.systems.BrickSystem
import io.bennyoe.ui.MenuUi
import ktx.assets.async.AssetStorage
import ktx.graphics.use
import ktx.log.logger

private val LOG = logger<MenuScreen>()
class MenuScreen(game: Main, val assets: AssetStorage = game.assets) : Screen(game) {
    private val bg = Texture("images/splash-screen.jpg")
    private val ui by lazy { MenuUi(this) }
    var selectedLevel: BearoutMap? = null

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            game.removeScreen<MenuScreen>()
            game.addScreen(LevelDesignerScreen(game, assets))
            game.setScreen<LevelDesignerScreen>()
        }
//        val isKeyboardControl = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
//        val isMouseControl = Gdx.input.justTouched()
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            game.removeScreen<MenuScreen>()
            game.addScreen(GameOverScreen(game, 0))
            game.setScreen<GameOverScreen>()
        }

        game.engine.getSystem(BrickSystem::class.java).selectedLevel = selectedLevel
        LOG.debug { selectedLevel.toString() }
//        if (isKeyboardControl || isMouseControl) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

            game.removeScreen<MenuScreen>()
            dispose()
            game.addScreen(GameScreen(game, assets, true))
            game.setScreen<GameScreen>()
        }
//        }

        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }
}
