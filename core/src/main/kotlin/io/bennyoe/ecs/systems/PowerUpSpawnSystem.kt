package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.UNIT_SCALE
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.log.logger

private val LOG = logger<PowerUpSpawnSystem>()

class PowerUpSpawnSystem(
    val powerUpTextureAtlas: TextureAtlas,
) : IteratingSystem(
    allOf(BrickComponent::class, TransformComponent::class).get()
) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val brick = entity[BrickComponent.mapper]!!
        val transform = entity[TransformComponent.mapper]!!
        if (brick.hitPoints <= 0) {
            if (brick.hasPowerUp) {
//                audioService.play(SoundAsset.POWER_UP_FALLING)
                engine.entity {
                    with<TransformComponent> {
                        position.x = (transform.position.x + transform.size.x / 2) - (16 * UNIT_SCALE)
                        position.y = (transform.position.y + transform.size.y / 2) - (16 * UNIT_SCALE)
                        position.z = -2f
                        LOG.info { "Power-Up created at position: ${position.x}, ${position.y}" }
                    }
                    with<PowerUpComponent> {
                        powerUpType = brick.powerUpType
                    }
                    with<GraphicComponent> {
                        sprite.run {
                            when(brick.powerUpType) {
                                PowerUpType.SHOOTER -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.SHOOTER.atlasKey))
                                PowerUpType.PENETRATION -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.PENETRATION.atlasKey))
                                PowerUpType.FAST_BALL -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.FAST_BALL.atlasKey))
                                PowerUpType.MULTIBALL -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.MULTIBALL.atlasKey))
                                PowerUpType.BONUS_HEART -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.BONUS_HEART.atlasKey))
                                PowerUpType.CHANGE_SIZE -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.CHANGE_SIZE.atlasKey))
                                PowerUpType.STICKY_BALL -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.STICKY_BALL.atlasKey))
                                PowerUpType.EXPLODING_BALL -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.EXPLODING_BALL.atlasKey))
                                PowerUpType.REVERSE_CONTROL -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.REVERSE_CONTROL.atlasKey))
                                PowerUpType.SHEEP -> setRegion(powerUpTextureAtlas.findRegion(PowerUpType.SHEEP.atlasKey))
                            }
                            setOriginCenter()
                        }
                    }
                }
            }
        }
    }
}

