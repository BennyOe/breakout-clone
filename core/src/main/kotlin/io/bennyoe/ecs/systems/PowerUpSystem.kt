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

private val LOG = logger<PowerUpSystem>()

class PowerUpSystem(
    val powerUpTextureAtlas: TextureAtlas
) : IteratingSystem(
    allOf(BrickComponent::class, TransformComponent::class).get()
) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val brick = entity[BrickComponent.mapper]!!
        val transform = entity[TransformComponent.mapper]!!
        if (brick.hitpoints <= 0) {
            if (brick.hasPowerUp) {
                engine.entity {
                    with<TransformComponent> {
                        position.x = (transform.position.x + transform.size.x / 2) - (16 * UNIT_SCALE)
                        position.y = (transform.position.y + transform.size.y / 2) - (16 * UNIT_SCALE)
                        LOG.info { "Power-Up created at position: ${position.x}, ${position.y}" }
                    }
                    with<PowerUpComponent> {
                        powerUpType = PowerUpType.SHOOTER
                    }
                    with<GraphicComponent> {
                        sprite.run {
                            setRegion(powerUpTextureAtlas.findRegion("honeypot_trans"))
                            setOriginCenter()
                        }
                    }
                }
            }
        }
    }
}

