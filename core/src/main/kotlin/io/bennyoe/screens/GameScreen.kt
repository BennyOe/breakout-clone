package io.bennyoe.screens

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import io.bennyoe.Main
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.assets.AnimationAsset
import io.bennyoe.assets.MusicAsset
import io.bennyoe.assets.TextureAsset
import io.bennyoe.assets.TextureAtlasAsset
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.systems.AnimationSystem
import io.bennyoe.ecs.systems.BrickCollisionSystem
import io.bennyoe.ecs.systems.DebugSystem
import io.bennyoe.ecs.systems.ExplosionSystem
import io.bennyoe.ecs.systems.GameStateSystem
import io.bennyoe.ecs.systems.PlayerCollisionSystem
import io.bennyoe.ecs.systems.PlayerInputSystem
import io.bennyoe.ecs.systems.PowerUpCollisionSystem
import io.bennyoe.ecs.systems.PowerUpSpawnSystem
import io.bennyoe.ecs.systems.ShooterCollisionSystem
import io.bennyoe.ui.GameUI
import ktx.ashley.allOf
import ktx.assets.async.AssetStorage
import ktx.graphics.use
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1 / 20f

class GameScreen(game: Main, val assets: AssetStorage, val isKeyboard: Boolean) : Screen(game) {
    private val background = assets[TextureAsset.BACKGROUND.descriptor]
    private val ballsAtlas by lazy { assets[TextureAtlasAsset.BALLS.descriptor] }
    private val powerUpsAtlas by lazy { assets[TextureAtlasAsset.POWERUPS.descriptor] }
    private val explosionAtlas by lazy { assets[AnimationAsset.EXPLOSION.descriptor] }
    private val gameStateSystem by lazy { GameStateSystem(audioService, game, ballsAtlas) }
    private val player by lazy { engine.getEntitiesFor(allOf(PlayerComponent::class).get()).firstOrNull() }
    private val ui by lazy { GameUI() }

    override fun show() {
        engine.addSystem(gameStateSystem)
        if (player == null) {
            LOG.error { "Player entity not found! Make sure GameStateSystem creates it." }
            return
        }

        val playerCollisionSystem = PlayerCollisionSystem(player!!, audioService)
        engine.addSystem(playerCollisionSystem)

        val brickEntities = engine.getEntitiesFor(allOf(BrickComponent::class).get())
        val brickCollisionSystem = BrickCollisionSystem(brickEntities, audioService, gameStateSystem)

        audioService.play(MusicAsset.BG_MUSIC, 0.25f)

        engine.addSystem(brickCollisionSystem)
        engine.addSystem(ShooterCollisionSystem(gameStateSystem))
        engine.addSystem(ExplosionSystem(brickEntities, audioService, gameStateSystem))
        engine.addSystem(AnimationSystem(explosionAtlas))
        engine.addSystem(PowerUpSpawnSystem(powerUpsAtlas))
        engine.addSystem(PowerUpCollisionSystem(player!!, assets, audioService, gameStateSystem))
        engine.addSystem(DebugSystem(powerUpsAtlas))
        engine.addSystem(PlayerInputSystem(viewport, isKeyboard))
        setupUserInterface()
    }

    override fun hide() {
        stage.clear()
    }

    override fun render(delta: Float) {
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
    }

    private fun setupUserInterface() {
        stage.addActor(ui)
    }
}
