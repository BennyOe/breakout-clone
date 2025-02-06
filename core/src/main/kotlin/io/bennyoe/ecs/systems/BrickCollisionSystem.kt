package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.AnimationComponent
import io.bennyoe.ecs.components.AnimationType
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.ExplodingComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.log.logger
import kotlin.math.abs

private const val SAFETY_MARGIN = 0.0f
private val LOG = logger<BrickCollisionSystem>()

class BrickCollisionSystem(
    private val brickEntities: ImmutableArray<Entity>,
    private val audioService: AudioService,
    private val gameStateSystem: GameStateSystem
) : IteratingSystem(
    allOf(BallComponent::class, TransformComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val ball = entity[BallComponent.mapper]
        require(ball != null) { "entity has no ball entity" }

        for (brickEntity in brickEntities) {
            intersectsBrick(transform, brickEntity, ball, deltaTime)
        }
    }

    private fun intersectsBrick(
        transform: TransformComponent,
        brickEntity: Entity,
        ball: BallComponent,
        deltaTime: Float
    ) {
        val brickTransform = brickEntity[TransformComponent.mapper]!!
        val brick = brickEntity[BrickComponent.mapper]!!

        if (checkCollision(brickTransform, transform, ball, deltaTime)) {
            audioService.play(SoundAsset.BRICK_HIT)

            val ballYMiddle = transform.position.y + (transform.size.y / 2)
            val ballXMiddle = transform.position.x + (transform.size.x / 2)

            val brickBottom = brickTransform.position.y
            val brickTop = brickTransform.position.y + brickTransform.size.y
            val brickYMiddle = brickTransform.position.y + (brickTransform.size.y / 2)
            val brickLeft = brickTransform.position.x
            val brickRight = brickTransform.position.x + brickTransform.size.x
            val brickXMiddle = brickTransform.position.x + (brickTransform.size.x / 2)

            gameStateSystem.addScore(1)
            brick.hitpoints--

            if (ball.isPenetrating) return

            val overlapX = abs(ballXMiddle - brickXMiddle) - (brickTransform.size.x / 2 + transform.size.x / 2)
            val overlapY = abs(ballYMiddle - brickYMiddle) - (brickTransform.size.y / 2 + transform.size.y / 2)

            if (abs(overlapX) < abs(overlapY)) {
                // horizontal hit
                if (ballXMiddle > brickXMiddle) {
                    // hit from the right
                    transform.position.x = brickRight + SAFETY_MARGIN
                } else {
                    transform.position.x = brickLeft - transform.size.x - SAFETY_MARGIN
                }
                reverseX(ball)
            } else {
                // vertical hit
                if (ballYMiddle > brickYMiddle) {
                    // hit from the top
                    transform.position.y = brickTop + SAFETY_MARGIN
                } else {
                    transform.position.y = brickBottom - transform.size.y - SAFETY_MARGIN
                }
                reverseY(ball)
            }
            if (ball.isExploding) {
                engine.entity {
                    with<TransformComponent> {
                        size.set(20f, 20f)
                        setInitialPosition(brickXMiddle - 10, brickYMiddle - 8, 0f)
                    }
                    with<AnimationComponent> {
                        type = AnimationType.EXPLOSION
                    }
                    with<GraphicComponent>()
                }
                val explodingComponent = brickEntity[ExplodingComponent.mapper]!!
                explodingComponent.isExploding = true
            }
        }
    }
}

private fun reverseX(ball: BallComponent) {
    ball.xSpeed *= -1
}

private fun reverseY(ball: BallComponent) {
    ball.ySpeed *= -1
}

private fun checkCollision(
    brickTransform: TransformComponent,
    ballTransform: TransformComponent,
    ball: BallComponent,
    deltaTime: Float
): Boolean {
    // Aktuelles Rechteck des Balls
    val ballBounds = Rectangle(
        ballTransform.position.x,
        ballTransform.position.y,
        ballTransform.size.x,
        ballTransform.size.y
    )

    // Zielposition des Balls nach Bewegung
    val nextPositionX = ballTransform.position.x + ball.xSpeed * deltaTime
    val nextPositionY = ballTransform.position.y + ball.ySpeed * deltaTime

    // Rechteck des bewegten Balls
    val expandedBounds = Rectangle(
        minOf(ballBounds.x, nextPositionX),
        minOf(ballBounds.y, nextPositionY),
        ballBounds.width + abs(ball.xSpeed * deltaTime),
        ballBounds.height + abs(ball.ySpeed * deltaTime)
    )

    // Rechteck des Bricks
    val brickBounds = Rectangle(
        brickTransform.position.x,
        brickTransform.position.y,
        brickTransform.size.x,
        brickTransform.size.y
    )

    // Prüfen, ob der erweiterte Ball-Rechteckstrahl mit dem Brick überlappt
    return Intersector.overlaps(expandedBounds, brickBounds)
}

