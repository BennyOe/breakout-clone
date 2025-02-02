package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger

private const val SAFETY_MARGIN = 0.01f
private val LOG = logger<SimpleCollisionSystem>()

class SimpleCollisionSystem(
    val viewport: Viewport,
    private val audioService: AudioService
) : IteratingSystem(
    allOf(BallComponent::class, TransformComponent::class, GraphicComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val ball = entity[BallComponent.mapper]
        require(ball != null) { "entity has no ball entity" }

        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val graphics = entity[GraphicComponent.mapper]
        require(graphics != null) { "entity has no graphics entity" }

        checkScreenCollision(transform, ball)
        checkBallCollision(entities, ball, transform)

    }

    private fun checkBallCollision(entities: ImmutableArray<Entity>, ball: BallComponent, transform: TransformComponent) {
        for (otherEntity in entities) {
            val otherBall = otherEntity[BallComponent.mapper]
            val otherTransform = otherEntity[TransformComponent.mapper]

            if (otherBall != null && otherTransform != null && ball != otherBall) {
                val ballRect = Rectangle(
                    transform.position.x,
                    transform.position.y,
                    transform.size.x,
                    transform.size.y
                )

                val otherBallRect = Rectangle(
                    otherTransform.position.x,
                    otherTransform.position.y,
                    otherTransform.size.x,
                    otherTransform.size.y
                )
                if (Intersector.overlaps(ballRect, otherBallRect)) {
                    if (transform.position.x + SAFETY_MARGIN > otherTransform.position.x) {
                        transform.position.x += SAFETY_MARGIN
                    } else {
                        transform.position.x -= SAFETY_MARGIN
                    }

                    if (transform.position.y + SAFETY_MARGIN > otherTransform.position.y) {
                        transform.position.y += SAFETY_MARGIN
                    } else {
                        transform.position.y -= SAFETY_MARGIN
                    }

                    reverseX(ball)
                    reverseY(ball)
                }
            }
        }
    }

    private fun checkScreenCollision(transform: TransformComponent, ball: BallComponent) {
        // left collision
        if (transform.position.x <= 0) {
            transform.position.x = 0f + SAFETY_MARGIN
            reverseX(ball)
            audioService.play(SoundAsset.WALL_HIT)
        }
        // right collision
        if (transform.position.x >= viewport.worldWidth - transform.size.x) {
            transform.position.x = viewport.worldWidth - transform.size.x + SAFETY_MARGIN
            reverseX(ball)
            audioService.play(SoundAsset.WALL_HIT)
        }
        // top collision
        if (transform.position.y >= viewport.worldHeight - transform.size.y - SAFETY_MARGIN) {
            transform.position.y = viewport.worldHeight - transform.size.y + SAFETY_MARGIN
            reverseY(ball)
            audioService.play(SoundAsset.WALL_HIT)
        }
        // bottom collision
//        if (transform.position.y <= 0) {
//            transform.position.y = 0f + SAFETY_MARGIN
//            reverseY(ball)
//            audioService.play(SoundAsset.WALL_HIT)
//            val player = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).first()
//            val playerComponent = player[PlayerComponent.mapper]!!
//            playerComponent.lives--
//            LOG.debug { "Lives reduced to ${playerComponent.lives}" }
//        }
    }

    private fun reverseX(ball: BallComponent) {
        ball.xSpeed *= -1
    }

    private fun reverseY(ball: BallComponent) {
        ball.ySpeed *= -1
    }
}
