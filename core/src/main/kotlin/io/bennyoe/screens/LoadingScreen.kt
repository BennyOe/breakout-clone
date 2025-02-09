package io.bennyoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import io.bennyoe.Main
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.assets.AnimationAsset
import io.bennyoe.assets.MusicAsset
import io.bennyoe.assets.SoundAsset
import io.bennyoe.assets.TextureAsset
import io.bennyoe.assets.TextureAtlasAsset
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.graphics.use
import ktx.log.logger
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: Main, val assets: AssetStorage = game.assets) : Screen(game) {
    private val bg = Texture("images/splash-screen.jpg")
    private lateinit var pressToBegin: Label

    override fun show() {
        super.show()
        val oldTime = System.currentTimeMillis()
        val assetRefs = gdxArrayOf(
            AnimationAsset.entries.map { assets.loadAsync(it.descriptor) },
            TextureAsset.entries.map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.entries.map { assets.loadAsync(it.descriptor) },
            SoundAsset.entries.map { assets.loadAsync(it.descriptor) },
            MusicAsset.entries.map { assets.loadAsync(it.descriptor) },
        ).flatten()
        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.debug { "It took ${System.currentTimeMillis() - oldTime}ms to load the assets" }
            assetsLoaded()
        }

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
                row()
                pressToBegin = label("Press H to show the high-score", "default") {
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

        if (assets.progress.isFinished) {
            val isKeyboardControl = Gdx.input.isKeyJustPressed(Keys.SPACE)
            val isMouseControl = Gdx.input.justTouched()
            if (Gdx.input.isKeyJustPressed(Keys.H)) {
                game.removeScreen<LoadingScreen>()
                game.addScreen(GameOverScreen(game, 0))
                game.setScreen<GameOverScreen>()
            }

            if (isKeyboardControl || isMouseControl) {
                game.removeScreen<LoadingScreen>()
                dispose()
                game.addScreen(GameScreen(game, assets, isKeyboardControl))
                game.setScreen<GameScreen>()
            }
        }
        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    private fun assetsLoaded() {
        pressToBegin += forever(sequence(Actions.fadeIn(0.5f) + fadeOut(0.5f)))
            audioService.play(MusicAsset.BG_MUSIC, 1f)
    }
}
