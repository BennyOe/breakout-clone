package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
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
import ktx.log.logger

private val LOG = logger<MultiballEffect>()

class MultiballEffect(private val audioService: AudioService) : PowerUpEffect() {
    override val isAdditionalEffect = true
    private val ballsAtlas = TextureAtlas("sprites/balls.atlas")
    override val powerUpType = PowerUpType.MULTIBALL
    override var remainingTime = 5f

    override fun apply(playerEntity: Entity, engine: Engine) {
        val ballEntity = engine.getEntitiesFor(Family.all(BallComponent::class.java).get()).first()
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
                if (ball.isSticky) {
                    xSpeed = 4f
                    ySpeed = 4f
                    acceleration = 2.2f
                } else {
                    xSpeed = ball.xSpeed * -1
                    ySpeed = ball.ySpeed
                    acceleration = ball.acceleration
                }
            }
        }
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) = Unit
}
