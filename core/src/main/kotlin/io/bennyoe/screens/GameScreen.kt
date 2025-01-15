package io.bennyoe.screens

import com.badlogic.gdx.graphics.Texture
import io.bennyoe.Main
import io.bennyoe.UNIT_SCALE
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.with

class GameScreen(game: Main) : Screen(game) {
    private val playerTexture = Texture("pedal2.png")
    private val ballTexture = Texture("ball.png")

    override fun show() {
        val player = engine.entity {
            with<TransformComponent> {
                position.set(1f, 1f, 0f)
                size.set(512 * UNIT_SCALE, 128 * UNIT_SCALE)
            }
            with<GraphicComponent> {
                sprite.run {
                    setRegion(playerTexture)
                    setOriginCenter()
                }
            }
            with<PlayerComponent>()
        }
        val ball = engine.entity {
            with<TransformComponent> {
                position.set(3f, 3f, 0f)
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

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun dispose() {
        playerTexture.dispose()
    }
}
