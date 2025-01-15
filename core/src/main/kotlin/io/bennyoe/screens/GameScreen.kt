package io.bennyoe.screens

import com.badlogic.gdx.graphics.Texture
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
import io.bennyoe.ecs.systems.PlayerCollisionSystem
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.getSystem
import ktx.ashley.with
import ktx.log.logger

private val LOG = logger<GameScreen>()

class GameScreen(game: Main) : Screen(game) {
    private val playerTexture = Texture("baer.png")
    private val ballTexture = Texture("ball.png")

    override fun show() {
        val brickSystem = engine.getSystem<BrickSystem>()
        brickSystem.initializeBricks()
        val player = engine.entity {
            with<TransformComponent> {
                position.set(1f, 1f, 0f)
                size.set(600 * UNIT_SCALE, 215 * UNIT_SCALE)
            }
            with<GraphicComponent> {
                sprite.run {
                    setRegion(playerTexture)
                    setOriginCenter()
                }
            }
            with<PlayerComponent>()
        }

        val playerCollisionSystem = PlayerCollisionSystem(viewport, player)
        engine.addSystem(playerCollisionSystem)

        val brickEntities = engine.getEntitiesFor(allOf(BrickComponent::class).get())
        val brickCollisionSystem = BrickCollisionSystem(viewport, brickEntities)
        engine.addSystem(brickCollisionSystem)

        repeat(100) {
            engine.entity {
                with<TransformComponent> {
                    position.set(random(0, WORLD_WIDTH.toInt()).toFloat(), random(1, WORLD_HEIGHT.toInt()).toFloat(), 0f)
                    size.set(128 * UNIT_SCALE, 128 * UNIT_SCALE)
                }
                with<GraphicComponent> {
                    sprite.run {
                        setRegion(ballTexture)
                        setOriginCenter()
                    }
                }
                with<BallComponent> {

                }
            }
        }
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun dispose() {
        playerTexture.dispose()
    }
}
