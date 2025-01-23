package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
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
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.utillity.Mapper.Companion.mapToRange
import kotlin.math.cos
import kotlin.math.sin

private val LOG = logger<BallComponent>()
private const val UPDATE_RATE = 1 / 80f

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

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= UPDATE_RATE) {
            accumulator -= UPDATE_RATE

            entities.forEach { entity ->
                entity[TransformComponent.mapper]?.let { transform ->
                    transform.prevPosition.set(transform.position)
                }
            }
            super.update(UPDATE_RATE)
        }

        val alpha = accumulator / UPDATE_RATE
        entities.forEach { entity ->
            entity[TransformComponent.mapper]?.let { transform ->
                transform.interpolatedPosition.set(
                    MathUtils.lerp(transform.prevPosition.x, transform.position.x, alpha),
                    MathUtils.lerp(transform.prevPosition.y, transform.position.y, alpha),
                    transform.position.z
                )
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val ball = entity[BallComponent.mapper]
        val powerUp = entity[PowerUpComponent.mapper]
        val bullet = entity[BulletComponent.mapper]
        val playerEntity = engine.getEntitiesFor(allOf(PlayerComponent::class).get())[0]
        val playerTransform = playerEntity[TransformComponent.mapper]!!


//        LOG.info { "Ball Speed: ${ball?.xSpeed}" }
        if (ball != null) {
            if (ball.isSticky) {
                ballFollowsPlayer(transform, playerTransform, ball)
            } else {
                transform.position.x += if (ball.xSpeed > 0) (ball.xSpeed * deltaTime * ball.acceleration + ball.boost) else (ball.xSpeed * deltaTime * ball.acceleration - ball.boost)
                transform.position.y += if (ball.ySpeed > 0) (ball.ySpeed * deltaTime * ball.acceleration + ball.boost) else (ball.ySpeed * deltaTime * ball.acceleration - ball.boost)
            }
        }

        if (powerUp != null) {
            transform.position.y -= random(4, 9).toFloat() * deltaTime
        }

        if (bullet != null) {
            transform.position.y += 22f * deltaTime
        }
    }

    private fun ballFollowsPlayer(
        transform: TransformComponent,
        playerTransform: TransformComponent,
        ball: BallComponent
    ) {
        if (ball.offsetXToPlayer == 0f) {
            ball.offsetXToPlayer = transform.position.x - playerTransform.position.x
        }

        transform.position.x = playerTransform.position.x + ball.offsetXToPlayer
        transform.position.y = playerTransform.position.y + playerTransform.size.y

        ball.xSpeed = 0f
        ball.ySpeed = 0f

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            ball.isSticky = false

            val angle = mapToRange(ball.offsetXToPlayer, 0f, playerTransform.size.x, Math.toRadians(140.0).toFloat(), Math.toRadians(40.0).toFloat())
            ball.xSpeed = 6 * cos(angle) * 1.8f
            ball.ySpeed = 6 * sin(angle) * 1.8f
            ball.offsetXToPlayer = 0f
        }
    }
}
