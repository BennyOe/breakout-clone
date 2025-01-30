package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.ExplodingComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<ExplosionSystem>()

class ExplosionSystem(
    private val brickEntities: ImmutableArray<Entity>,
    private val audioService: AudioService
) : IteratingSystem(
    allOf(ExplodingComponent::class).get()
) {

    override fun processEntity(entity: Entity, deltaTime: Float) {

        val explodingComponent = entity[ExplodingComponent.mapper]!!
        val transform = entity[TransformComponent.mapper]!!

        if (!explodingComponent.isExploding) return

        brickEntities.forEach { brickEntity ->
            val brickTransform = brickEntity[TransformComponent.mapper]!!
            val distance = transform.position.dst(brickTransform.position)
            if (distance <= explodingComponent.explosionRadius) {
                audioService.play(SoundAsset.EXPLOSION1)
                LOG.info { "Brick exploded at ${brickTransform.position}" }
                brickEntity[BrickComponent.mapper]?.hitpoints = 0
            }
        }
        explodingComponent.isExploding = false
    }
}
