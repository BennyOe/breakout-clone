package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.UNIT_SCALE
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with

class MultiballEffect(private val audioService: AudioService) : PowerUpEffect {
    private val ballsAtlas = TextureAtlas("sprites/balls.atlas")

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val ballTransform = ballEntity[TransformComponent.mapper]!!
        val ball = ballEntity[BallComponent.mapper]!!

        audioService.play(SoundAsset.PU_MULTIBALL)

        engine.entity {
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.MULTIBALL
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(ballTransform.position.x + 1, ballTransform.position.y + 1, 0f)
                size.set(32 * UNIT_SCALE, 32 * UNIT_SCALE)
            }
            with<GraphicComponent> {
                sprite.run {
                    setRegion(ballsAtlas.findRegion("Ball_Yellow_Glossy_trans-32x32"))
                    setOriginCenter()
                }
            }
            with<BallComponent> {
                xSpeed = ball.xSpeed * -1
                ySpeed = ball.ySpeed
                acceleration = ball.acceleration
            }
        }
    }
}
