package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.abs

private const val SAFETY_MARGIN = 0.001f

class BrickCollisionSystem(
    private val viewport: Viewport,
    private val brickEntities: ImmutableArray<Entity>
) : IteratingSystem(
    allOf(BallComponent::class, TransformComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val ball = entity[BallComponent.mapper]
        require(ball != null) { "entity has no ball entity" }

        for (brickEntity in brickEntities) {
            val brickTransform = brickEntity[TransformComponent.mapper]!!
            val brick = brickEntity[BrickComponent.mapper]!!
            intersectsPedal(transform, brickTransform, brick, ball)
        }
    }

    private fun intersectsPedal(transform: TransformComponent, brickTransform: TransformComponent, brick: BrickComponent, ball: BallComponent) {
        if (Intersector.overlaps(
                Rectangle(
                    transform.position.x,
                    transform.position.y,
                    transform.size.x,
                    transform.size.y
                ),
                Rectangle(
                    brickTransform.position.x,
                    brickTransform.position.y,
                    brickTransform.size.x,
                    brickTransform.size.y
                )
            )
        ) {
            // TODO has to be checked. The detection is not working right
            brick.hitpoints--
            if (abs(transform.position.y - brickTransform.position.y) > abs(
                    transform.position.y - (brickTransform.position.y + brickTransform.size
                        .y)
                )
            ) {
                // ball is hitting the brick on the bottom
                transform.position.y = brickTransform.position.y + transform.size.y + brickTransform.size.y + SAFETY_MARGIN
                reverseY(ball)
            } else if (abs(transform.position.y - brickTransform.position.y) < abs(
                    transform.position.y - (brickTransform.position.y +
                        brickTransform.position.y
                        )
                )
            ) { // ball is hitting the brick from top
                transform.position.y = brickTransform.position.y - transform.size.y - SAFETY_MARGIN
                reverseY(ball)
            } else if (abs(transform.position.x - brickTransform.position.x) > abs(
                    transform.position.x - (brickTransform.position.x +
                        brickTransform.position.x)
                )
            ) { // ball is hitting the brick on the right
                transform.position.x = brickTransform.position.x + transform.size.x + brickTransform.size.x + SAFETY_MARGIN
                reverseX(ball)
            } else { // Ball is hitting brick on the left
                transform.position.x = brickTransform.position.x - transform.size.x - SAFETY_MARGIN
                reverseX(ball)
            }
        }
    }

    private fun reverseX(ball: BallComponent) {
        ball.xSpeed *= -1
    }

    private fun reverseY(ball: BallComponent) {
        ball.ySpeed *= -1
    }

}
