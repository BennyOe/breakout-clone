package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.graphics.Texture
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.BulletComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with

class ShooterSystem(private val audioService: AudioService) : IntervalIteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).get(), 0.5f) {
    private val laserTexture = Texture("images/laser.png")

    override fun processEntity(entity: Entity) {
        val transform = entity[TransformComponent.mapper]!!
        audioService.play(SoundAsset.FLINT3)

        engine.entity {
            with<TransformComponent>() {
                position.x = transform.position.x
                position.y = transform.position.y
                size.x = 0.5f
                size.y = 1f
            }
            with<BulletComponent>()
            with<GraphicComponent>() {
                sprite.run {
                    setRegion(laserTexture)
                    setOriginCenter()
                }
            }
        }
        engine.entity {
            with<TransformComponent>() {
                position.x = transform.position.x + transform.size.x - 0.5f
                position.y = transform.position.y
                size.x = 0.5f
                size.y = 1f
            }
            with<BulletComponent>()
            with<GraphicComponent>() {
                sprite.run {
                    setRegion(laserTexture)
                    setOriginCenter()
                }
            }
        }
    }
}
