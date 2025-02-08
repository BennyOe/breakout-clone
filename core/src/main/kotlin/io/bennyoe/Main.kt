package io.bennyoe

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import io.bennyoe.assets.BitmapFontAsset
import io.bennyoe.assets.TextureAtlasAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.audio.DefaultAudioService
import io.bennyoe.ecs.systems.BrickSystem
import io.bennyoe.ecs.systems.MoveSystem
import io.bennyoe.ecs.systems.PowerUpTextSystem
import io.bennyoe.ecs.systems.RenderSystem
import io.bennyoe.ecs.systems.SimpleCollisionSystem
import io.bennyoe.ecs.systems.TimerSystem
import io.bennyoe.screens.LoadingScreen
import io.bennyoe.ui.createSkin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf

const val UNIT_SCALE = 1 / 32f
const val WORLD_WIDTH = 48f
const val WORLD_HEIGHT = 27f
const val GAME_WIDTH = WORLD_WIDTH / UNIT_SCALE
const val GAME_HEIGHT = WORLD_HEIGHT / UNIT_SCALE

class Main : KtxGame<KtxScreen>() {
    // for Scene2D
    val uiViewport by lazy { FitViewport(WORLD_WIDTH / UNIT_SCALE, WORLD_HEIGHT / UNIT_SCALE) }
    val stage by lazy {
        val result = Stage(uiViewport, batch)
        Gdx.input.inputProcessor = result
        result
    }
    // ======
    val viewport by lazy { FitViewport(WORLD_WIDTH, WORLD_HEIGHT) }
    val batch: Batch by lazy { SpriteBatch() }
    val assets: AssetStorage by lazy {
        AssetStorage()
    }
    val preferences: Preferences by lazy { Gdx.app.getPreferences("bearout") }
    val audioService: AudioService by lazy { DefaultAudioService(assets) }
    val engine: Engine by lazy {
        PooledEngine().apply {
            addSystem(BrickSystem(assets))
            addSystem(SimpleCollisionSystem(viewport, audioService))
            addSystem(MoveSystem(audioService))
            addSystem(RenderSystem(batch, viewport))
            addSystem(TimerSystem())
            addSystem(PowerUpTextSystem())
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        KtxAsync.initiate()
        val assetRefs = gdxArrayOf(
            TextureAtlasAsset.entries.filter { it.isSkinAtlas }.map { assets.loadAsync(it.descriptor) },
            BitmapFontAsset.entries.map { assets.loadAsync(it.descriptor) }
        ).flatten()

        KtxAsync.launch {
            assetRefs.joinAll()
            createSkin(assets)
            addScreen(LoadingScreen(this@Main, assets))
            setScreen<LoadingScreen>()
//            addScreen(GameOverScreen(this@Main, assets))
//            setScreen<GameOverScreen>()
        }
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        assets.dispose()
        stage.dispose()
    }
}
