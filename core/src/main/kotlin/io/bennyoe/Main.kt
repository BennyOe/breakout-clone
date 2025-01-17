package io.bennyoe

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application.LOG_DEBUG
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import io.bennyoe.ecs.systems.BrickSystem
import io.bennyoe.ecs.systems.MoveSystem
import io.bennyoe.ecs.systems.PlayerInputSystem
import io.bennyoe.ecs.systems.RenderSystem
import io.bennyoe.ecs.systems.SimpleCollisionSystem
import io.bennyoe.screens.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

const val UNIT_SCALE = 1 / 32f
const val WORLD_WIDTH = 48f
const val WORLD_HEIGHT = 27f

class Main : KtxGame<KtxScreen>() {
    val viewport by lazy { FitViewport(WORLD_WIDTH, WORLD_HEIGHT) }
    val batch: Batch by lazy { SpriteBatch() }
    val engine: Engine by lazy {
        PooledEngine().apply {
            addSystem(PlayerInputSystem(viewport))
            addSystem(BrickSystem(viewport))
            addSystem(SimpleCollisionSystem(viewport))
            addSystem(MoveSystem())
            addSystem(RenderSystem(batch, viewport))
        }
    }

    override fun create() {
        Gdx.app.logLevel = LOG_DEBUG
        KtxAsync.initiate()
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }
}

