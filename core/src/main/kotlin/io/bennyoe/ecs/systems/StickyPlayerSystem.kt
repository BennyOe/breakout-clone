package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class StickyPlayerSystem(playerEntity: Entity) : IteratingSystem(
    allOf(BallComponent::class, TransformComponent::class).get()
){
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]!!
        val ball = entity[BallComponent.mapper]!!
    }
}
