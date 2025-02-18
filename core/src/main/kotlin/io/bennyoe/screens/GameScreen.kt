package io.bennyoe.screens

import BearoutMap
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.bennyoe.Main
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.assets.AnimationAsset
import io.bennyoe.assets.MusicAsset
import io.bennyoe.assets.TextureAsset
import io.bennyoe.assets.TextureAtlasAsset
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.systems.AnimationSystem
import io.bennyoe.ecs.systems.BrickCollisionSystem
import io.bennyoe.ecs.systems.BrickSystem
import io.bennyoe.ecs.systems.ColorOverlaySystem
import io.bennyoe.ecs.systems.DebugSystem
import io.bennyoe.ecs.systems.ExplosionSystem
import io.bennyoe.ecs.systems.GameStateSystem
import io.bennyoe.ecs.systems.MoveSystem
import io.bennyoe.ecs.systems.PlayerCollisionSystem
import io.bennyoe.ecs.systems.PlayerInputSystem
import io.bennyoe.ecs.systems.PowerUpCollisionSystem
import io.bennyoe.ecs.systems.PowerUpSpawnSystem
import io.bennyoe.ecs.systems.PowerUpTextSystem
import io.bennyoe.ecs.systems.ShooterCollisionSystem
import io.bennyoe.ecs.systems.SimpleCollisionSystem
import io.bennyoe.ecs.systems.SystemManager
import io.bennyoe.ecs.systems.TimerSystem
import io.bennyoe.ui.GameUI
import ktx.ashley.allOf
import ktx.assets.async.AssetStorage
import ktx.graphics.use
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1 / 20f

class GameScreen(
    game: Main,
    val assets: AssetStorage,
    private val selectedLevel: BearoutMap?,
    private val isTestMode: Boolean = false,
    private val score: Int = 0
) : Screen(game) {
    private val systemManager by lazy { SystemManager(engine) }
    private val background = assets[TextureAsset.BACKGROUND.descriptor]
    private val ballsAtlas by lazy { assets[TextureAtlasAsset.BALLS.descriptor] }
    private val powerUpsAtlas by lazy { assets[TextureAtlasAsset.POWERUPS.descriptor] }
    private val explosionAtlas by lazy { assets[AnimationAsset.EXPLOSION.descriptor] }
    private val player by lazy { engine.getEntitiesFor(allOf(PlayerComponent::class).get()).firstOrNull() }
    private val ui by lazy { GameUI(this, isTestMode) }

    override fun show() {
        Gdx.input.isCursorCatched = true
        Gdx.input.setCursorPosition(Gdx.graphics.width / 2, Gdx.graphics.height / 2)
        registerSystems()
        if (player == null) {
            LOG.error { "Player entity not found! Make sure GameStateSystem creates it." }
            return
        }
        audioService.play(MusicAsset.BG_MUSIC, 0.25f)
        setupUserInterface()
    }

    private fun registerSystems() {
        systemManager.addSystem(BrickSystem(assets, selectedLevel))
        val gameStateSystem = systemManager.addSystem(GameStateSystem(audioService, game, ballsAtlas, engine, isTestMode, assets, selectedLevel,
            _score = score)
        ) as
            GameStateSystem
        systemManager.addSystem(SimpleCollisionSystem(viewport, audioService))
        systemManager.addSystem(MoveSystem(audioService))
        systemManager.addSystem(BrickCollisionSystem(audioService, gameStateSystem))
        systemManager.addSystem(TimerSystem())
        systemManager.addSystem(ShooterCollisionSystem(gameStateSystem))
        systemManager.addSystem(ExplosionSystem(audioService, gameStateSystem))
        systemManager.addSystem(PlayerCollisionSystem(player!!, audioService))
        systemManager.addSystem(AnimationSystem(explosionAtlas))
        systemManager.addSystem(PowerUpSpawnSystem(powerUpsAtlas))
        systemManager.addSystem(PowerUpCollisionSystem(player!!, assets, audioService, gameStateSystem))
        systemManager.addSystem(DebugSystem(powerUpsAtlas))
        systemManager.addSystem(PlayerInputSystem(viewport))
        systemManager.addSystem(PowerUpTextSystem(engine))
    }

    override fun hide() {
        Gdx.input.isCursorCatched = false
        stage.clear()
        ColorOverlaySystem.color = Color.CLEAR
        engine.removeAllEntities()
        systemManager.removeAllSystems()
    }

    override fun render(delta: Float) {
        val gameStateSystem = engine.getSystem(GameStateSystem::class.java)
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            if (isTestMode) {
                game.removeScreen<GameScreen>()
                game.addScreen(LevelDesignerScreen(game, assets, selectedLevel))
                game.setScreen<LevelDesignerScreen>()
            } else {
                game.removeScreen<GameScreen>()
                game.addScreen(MenuScreen(game))
                game.setScreen<MenuScreen>()
            }
        }
        val player = engine.getEntitiesFor(allOf(PlayerComponent::class).get()).firstOrNull()
        if (player == null) {
            LOG.error { "Player entity not found! Make sure GameStateSystem creates it." }
            return
        }
        (game.batch as SpriteBatch).renderCalls = 0
        batch.use(viewport.camera.combined) {
            it.draw(background, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
        }
        engine.update(min(MAX_DELTA_TIME, delta))
        audioService.update()
        val playerComponent = player.getComponent(PlayerComponent::class.java)
        val lives = playerComponent?.lives ?: 0
        ui.refreshHearts(lives)
        ui.refreshScore(gameStateSystem.score)
//        LOG.info { "Rendercalls: ${(game.batch as SpriteBatch).renderCalls}" }
        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    override fun dispose() {
        assets.dispose()
        engine.removeAllSystems()
        super.dispose()
        this.dispose()
    }

    private fun setupUserInterface() {
        stage.addActor(ui)
    }

    fun backToLevelDesigner() {
        game.removeScreen<GameScreen>()
        game.addScreen(LevelDesignerScreen(game, assets, selectedLevel))
        game.setScreen<LevelDesignerScreen>()
    }
}
