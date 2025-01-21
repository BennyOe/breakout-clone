package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.oneOf
import ktx.log.logger
import com.badlogic.gdx.math.MathUtils.random
import io.bennyoe.ecs.components.BulletComponent

private val LOG = logger<BallComponent>()
private const val UPDATE_RATE = 1 / 60f

class MoveSystem : IteratingSystem(
    allOf(
        GraphicComponent::class,
        TransformComponent::class
    ).oneOf(
        BallComponent::class,
        PowerUpComponent::class,
        BulletComponent::class,
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

        val ball = entity[BallComponent.mapper]
        val powerUp = entity[PowerUpComponent.mapper]
        val bullet = entity[BulletComponent.mapper]

//        LOG.info { "Ball Speed: ${ball?.xSpeed}" }
        if (ball != null) {
            transform.position.x += if(ball.xSpeed > 0) (ball.xSpeed * deltaTime * ball.acceleration + ball.boost) else (ball.xSpeed * deltaTime * ball.acceleration - ball.boost)
            transform.position.y += if(ball.ySpeed > 0) (ball.ySpeed * deltaTime * ball.acceleration + ball.boost) else (ball.ySpeed * deltaTime * ball.acceleration - ball.boost)
        }

        if (powerUp != null) {
            transform.position.y -= random(4,9).toFloat() * deltaTime
        }

        if (bullet != null) {
            transform.position.y += 9f * deltaTime
        }
    }
}
