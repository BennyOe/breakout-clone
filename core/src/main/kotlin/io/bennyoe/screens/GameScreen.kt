package io.bennyoe.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils.random
import io.bennyoe.Main
import io.bennyoe.UNIT_SCALE
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.ecs.systems.AnimationSystem
import io.bennyoe.ecs.systems.BrickCollisionSystem
import io.bennyoe.ecs.systems.BrickSystem
import io.bennyoe.ecs.systems.DebugSystem
import io.bennyoe.ecs.systems.ExplosionSystem
import io.bennyoe.ecs.systems.PlayerCollisionSystem
import io.bennyoe.ecs.systems.PowerUpCollisionSystem
import io.bennyoe.ecs.systems.PowerUpSystem
import io.bennyoe.ecs.systems.PowerUpTextSystem
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.getSystem
import ktx.ashley.with
import ktx.graphics.use
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1 / 20f

class GameScreen(game: Main) : Screen(game) {
    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }
    private val background = Texture("images/bg2dark.jpg")
    private val ballsAtlas by lazy { TextureAtlas("sprites/balls.atlas") }
    private val bricksAtlas by lazy { TextureAtlas("sprites/bricks.atlas") }
    private val powerUpsAtlas by lazy { TextureAtlas("sprites/powerUps.atlas") }
    private val explosionAtlas by lazy { TextureAtlas("animation/explosion.atlas") }

    override fun show() {
        val player = createPlayer()
        val ball = createBall()

        val brickSystem = engine.getSystem<BrickSystem>()
        brickSystem.initializeBricks(bricksAtlas)

        val playerCollisionSystem = PlayerCollisionSystem(player)
        engine.addSystem(playerCollisionSystem)

        val brickEntities = engine.getEntitiesFor(allOf(BrickComponent::class).get())
        val brickCollisionSystem = BrickCollisionSystem(viewport, brickEntities)
        engine.addSystem(brickCollisionSystem)
        engine.addSystem(ExplosionSystem(brickEntities))
        engine.addSystem(AnimationSystem(explosionAtlas))
        engine.addSystem(PowerUpSystem(powerUpsAtlas))
        engine.addSystem(PowerUpTextSystem())
        engine.addSystem(PowerUpCollisionSystem(player, ball))
        engine.addSystem(DebugSystem(powerUpsAtlas))
    }

    override fun render(delta: Float) {
        (game.batch as SpriteBatch).renderCalls = 0
        batch.use(viewport.camera.combined) {
            it.draw(background, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
        }
        engine.update(min(MAX_DELTA_TIME, delta))
//        LOG.info { "Rendercalls: ${(game.batch as SpriteBatch).renderCalls}" }
    }

    override fun dispose() {
        background.dispose()
        ballsAtlas.dispose()
        bricksAtlas.dispose()
        powerUpsAtlas.dispose()
        playerAtlas.dispose()
    }

    private fun createPlayer(): Entity {
        return engine.entity {
            with<TransformComponent> {
                setInitialPosition(1f, 1f, 0f)
                size.set(128 * UNIT_SCALE, 32 * UNIT_SCALE)
            }
            with<GraphicComponent> {
                sprite.run {
                    setRegion(playerAtlas.findRegion("bear"))
                    setOriginCenter()
                }
            }
            with<PlayerComponent>()
        }
    }

    private fun createBall(): Entity {
        return engine.entity {
            with<TransformComponent> {
                setInitialPosition(random(0, WORLD_WIDTH.toInt()).toFloat(), random(1, WORLD_HEIGHT.toInt()).toFloat(), 0f)
                size.set(32 * UNIT_SCALE, 32 * UNIT_SCALE)
            }
            with<GraphicComponent> {
                sprite.run {
                    setRegion(ballsAtlas.findRegion("Ball_Yellow_Glossy_trans-32x32"))
                    setOriginCenter()
                }
            }
            with<BallComponent>()
        }
    }
}
