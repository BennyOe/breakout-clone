package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.with

class DebugSystem(
    private val powerUpTextureAtlas: TextureAtlas
) : EntitySystem()
 {
    init {
        setProcessing(true)
    }

     override fun update(deltaTime: Float) {
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) -> {
                engine.entity {
                    with<PowerUpComponent>() {
                        powerUpType = PowerUpType.MULTIBALL
                    }
                    with<TransformComponent>() {
                        setInitialPosition(8f, 8f, 0f)
                    }
                    with<GraphicComponent> {
                        sprite.run { setRegion(powerUpTextureAtlas.findRegion("multiball_rounded")) }
                    }
                }
            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_2) -> {
                engine.entity {
                    with<PowerUpComponent>() {
                        powerUpType = PowerUpType.EXPLODING_BALL
                    }
                    with<TransformComponent>() {
                        setInitialPosition(8f, 8f, 0f)
                    }
                    with<GraphicComponent> {
                        sprite.run { setRegion(powerUpTextureAtlas.findRegion("bomb_trans")) }
                    }
                }
            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_3) -> {
                engine.entity {
                    with<PowerUpComponent>() {
                        powerUpType = PowerUpType.PENETRATION
                    }
                    with<TransformComponent>() {
                        setInitialPosition(8f, 8f, 0f)
                    }
                    with<GraphicComponent> {
                        sprite.run { setRegion(powerUpTextureAtlas.findRegion("muscle_trans")) }
                    }
                }
            }
            Gdx.input.isKeyPressed(Input.Keys.NUM_4) -> {
                engine.entity {
                    with<PowerUpComponent>() {
                        powerUpType = PowerUpType.SHOOTER
                    }
                    with<TransformComponent>() {
                        setInitialPosition(8f, 8f, 0f)
                    }
                    with<GraphicComponent> {
                        sprite.run { setRegion(powerUpTextureAtlas.findRegion("flint_pxl_sml")) }
                    }
                }
            }
        }
    }
}
