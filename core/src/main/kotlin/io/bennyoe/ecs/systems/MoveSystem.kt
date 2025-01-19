package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<BallComponent>()
private const val UPDATE_RATE = 1 / 60f

class MoveSystem : IteratingSystem(
    allOf(
        BallComponent::class,
        GraphicComponent::class,
        TransformComponent::class
    ).get()
) {
    private var accumulator = 0f

//    override fun update(deltaTime: Float) {
//        accumulator += deltaTime
//        while (accumulator >= UPDATE_RATE) {
//            accumulator -= UPDATE_RATE
//            super.update(UPDATE_RATE)
//        }
////        LOG.info { "Update Rate: $accumulator" }
//    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "entity has no graphic entity" }

        val ball = entity[BallComponent.mapper]
        require(ball != null) { "entity has no ball entity" }

//        LOG.info { ball.acceleration.toString() }
        transform.position.x += (ball.xSpeed * deltaTime * ball.acceleration + ball.boost)
        transform.position.y += (ball.ySpeed * deltaTime * ball.acceleration + ball.boost)
    }
}
