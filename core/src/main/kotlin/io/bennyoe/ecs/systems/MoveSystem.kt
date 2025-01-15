package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class MoveSystem(
    private val viewport: Viewport
) : IteratingSystem(
    allOf(
        BallComponent::class,
        GraphicComponent::class,
        TransformComponent::class
    ).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "entity has no graphic entity" }

        val ball = entity[BallComponent.mapper]
        require(ball != null) { "entity has no ball entity" }

        transform.position.x += (ball.xSpeed * deltaTime * ball.acceleration + ball.boost).toFloat()
        transform.position.y += (ball.ySpeed * deltaTime * ball.acceleration + ball.boost).toFloat()
    }
}
