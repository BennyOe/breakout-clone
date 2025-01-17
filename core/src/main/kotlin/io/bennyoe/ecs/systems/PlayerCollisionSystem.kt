package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger
import kotlin.math.cos
import kotlin.math.sin

private const val SAFETY_MARGIN = 0.001f
private const val SLOW_DOWN_FACTOR = 0.15f
private val LOG = logger<PlayerCollisionSystem>()

class PlayerCollisionSystem(
    private val viewport: Viewport,
    playerEntity: Entity
) : IteratingSystem(
    allOf(BallComponent::class, TransformComponent::class).get()
) {
    private val playerTransform = playerEntity[TransformComponent.mapper]!!
    private val playerComponent = playerEntity[PlayerComponent.mapper]!!

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val ball = entity[BallComponent.mapper]
        require(ball != null) { "entity has no ball entity" }

        intersectsPedal(transform, ball)
    }

    private fun intersectsPedal(transform: TransformComponent, ball: BallComponent) {
        if (Intersector.overlaps(
                Rectangle(
                    transform.position.x,
                    transform.position.y,
                    transform.size.x,
                    transform.size.y
                ),
                Rectangle(
                    playerTransform.position.x,
                    playerTransform.position.y,
                    playerTransform.size.x,
                    playerTransform.size.y
                )
            )
        ) {
            val playerTop = playerTransform.position.y + playerTransform.size.y

            transform.position.y = playerTop + SAFETY_MARGIN
            val xDiff = transform.position.x - playerTransform.position.x
            val angle = map(xDiff, 0f, playerTransform.size.x, Math.toRadians(140.0).toFloat(), Math.toRadians(40.0).toFloat())

            ball.acceleration = when {
                playerComponent.acceleration <= ball.acceleration -> (ball.acceleration - SLOW_DOWN_FACTOR).coerceAtLeast(1.2f)
                else -> playerComponent.acceleration.coerceAtMost(2.5f)
            }

            LOG.info { ball.acceleration.toString() }
            ball.xSpeed = (6 * cos(angle) * ball.acceleration)
            ball.ySpeed = (6 * sin(angle) * ball.acceleration)
        }
    }
}

private fun map(value: Float, orgStart: Float, orgStop: Float, targetStart: Float, targetStop: Float): Float {
    return targetStart + (value - orgStart) * (targetStop - targetStart) / (orgStop - orgStart)
}
