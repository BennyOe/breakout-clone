package io.bennyoe.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
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
import io.bennyoe.ecs.systems.BrickCollisionSystem
import io.bennyoe.ecs.systems.BrickSystem
import io.bennyoe.ecs.systems.DebugSystem
import io.bennyoe.ecs.systems.ExplosionSystem
import io.bennyoe.ecs.systems.PlayerCollisionSystem
import io.bennyoe.ecs.systems.PowerUpCollisionSystem
import io.bennyoe.ecs.systems.PowerUpSystem
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.getSystem
import ktx.ashley.with
import ktx.graphics.use
import ktx.log.logger

private val LOG = logger<GameScreen>()

class GameScreen(game: Main) : Screen(game) {
    private val playerTexture = Texture("images/bear-long-border.png")
    private val background = Texture("images/bg2dark.jpg")
    private val ballsAtlas by lazy { TextureAtlas("sprites/balls.atlas") }
    private val bricksAtlas by lazy { TextureAtlas("sprites/bricks.atlas") }
    private val powerUpsAtlas by lazy { TextureAtlas("sprites/powerUps.atlas") }

    override fun show() {
        val player = createPlayer()
        val ball = createBall()

        val brickSystem = engine.getSystem<BrickSystem>()
        brickSystem.initializeBricks(bricksAtlas)

        val playerCollisionSystem = PlayerCollisionSystem(viewport, player)
        engine.addSystem(playerCollisionSystem)

        val brickEntities = engine.getEntitiesFor(allOf(BrickComponent::class).get())
        val brickCollisionSystem = BrickCollisionSystem(viewport, brickEntities)
        engine.addSystem(brickCollisionSystem)

        val explosionSystem = ExplosionSystem(brickEntities)
        engine.addSystem(explosionSystem)

        val powerUpSystem = PowerUpSystem(powerUpsAtlas)
        engine.addSystem(powerUpSystem)

        val powerUpCollisionSystem = PowerUpCollisionSystem(player, ball)
        engine.addSystem(powerUpCollisionSystem)

        val debugSystem = DebugSystem(powerUpsAtlas)
        engine.addSystem(debugSystem)
    }

    override fun render(delta: Float) {
        batch.use(viewport.camera.combined) {
            it.draw(background, 0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
        }
        engine.update(delta)
    }

    override fun dispose() {
        playerTexture.dispose()
        background.dispose()
        ballsAtlas.dispose()
        bricksAtlas.dispose()
        powerUpsAtlas.dispose()
    }

    private fun createPlayer(): Entity {
        return engine.entity {
            with<TransformComponent> {
                position.set(1f, 1f, 0f)
                size.set(128 * UNIT_SCALE, 32 * UNIT_SCALE)
            }
            with<GraphicComponent> {
                sprite.run {
                    setRegion(playerTexture)
                    setOriginCenter()
                }
            }
            with<PlayerComponent>()
        }
    }

    private fun createBall(): Entity {
        return engine.entity {
            with<TransformComponent> {
                position.set(random(0, WORLD_WIDTH.toInt()).toFloat(), random(1, WORLD_HEIGHT.toInt()).toFloat(), 0f)
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
