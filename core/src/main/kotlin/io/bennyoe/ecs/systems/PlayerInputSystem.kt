package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.utillity.Mapper.Companion.mapToRange
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger
import kotlin.math.abs

private val LOG = logger<PlayerInputSystem>()

class PlayerInputSystem(
    private val viewport: Viewport,
    private val isKeyboardControls: Boolean = true
) : IteratingSystem(
    allOf(PlayerComponent::class).get()
) {
    private val tmpVec = Vector3()
    private val pedalMaxPosition = Vector3()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val player = entity[PlayerComponent.mapper]
        require(player != null) { "entity has no player entity" }

        calculatePlayerBoundaries(transform)

        if (isKeyboardControls) {
            // define keyboard controls
            keyboardControl(player)

            if (player.lastXPosition == null) {
                player.lastXPosition = tmpVec.x
            }
            val deltaX = transform.position.x - player.lastXPosition!!
            player.lastXPosition = transform.position.x

            transform.position.x = (transform.position.x + player.speed * deltaTime).coerceIn(0f, pedalMaxPosition.x)
            transform.interpolatedPosition.x = transform.position.x
            calculateAccelerationKeyboard(deltaX, deltaTime, player)
        } else {
            // define mouse controls
            transformMousePosition()
            if (player.lastXMousePosition == null) {
                player.lastXMousePosition = tmpVec.x
            }
            val deltaX = tmpVec.x - player.lastXMousePosition!!
            player.lastXMousePosition = tmpVec.x

            transform.position.x = calculatePlayerPosition(player, deltaX, transform)
            transform.interpolatedPosition.x = transform.position.x
            calculateAccelerationMouse(deltaX, deltaTime, player)
        }
    }

    private fun keyboardControl(player: PlayerComponent) {
        when {
            Gdx.input.isKeyPressed(Input.Keys.LEFT) -> {
                val adjustment = if (player.isReversed) player.acceleration else -player.acceleration
                player.speed = (player.speed + adjustment).coerceIn(-player.maxSpeed, player.maxSpeed)
            }
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> {
                val adjustment = if (player.isReversed) -player.acceleration else player.acceleration
                player.speed = (player.speed + adjustment).coerceIn(-player.maxSpeed, player.maxSpeed)
            }
            else -> {
                player.speed = when {
                    player.speed > 0 -> (player.speed - player.deceleration).coerceAtLeast(0f)
                    player.speed < 0 -> (player.speed + player.deceleration).coerceAtMost(0f)
                    else -> 0f
                }
            }
        }
    }

    private fun transformMousePosition() {
        val mouseX = Gdx.input.x.toFloat()
        tmpVec.set(mouseX, 0f, 0f)
        viewport.unproject(tmpVec)
    }

    private fun calculatePlayerBoundaries(transform: TransformComponent) {
        pedalMaxPosition.set(Gdx.graphics.width.toFloat(), 0f, 0f)
        viewport.unproject(pedalMaxPosition)
        pedalMaxPosition.x -= transform.size.x
    }

    private fun calculatePlayerPosition(player: PlayerComponent, mouseDeltaX: Float, transform: TransformComponent): Float {
        return when {
            player.isReversed -> {
                val invertedMouseDeltaX = -mouseDeltaX
                (transform.position.x + invertedMouseDeltaX).coerceIn(0f, pedalMaxPosition.x)
            }

            else -> (transform.position.x + mouseDeltaX).coerceIn(0f, pedalMaxPosition.x)
        }
    }

    private fun calculateAccelerationMouse(deltaX: Float, deltaTime: Float, player: PlayerComponent) {
        val accDiff = abs(deltaX * deltaTime)
        player.acceleration = mapToRange(accDiff, 0f, 0.1f, 1f, 12f)
    }

    private fun calculateAccelerationKeyboard(deltaX: Float, deltaTime: Float, player: PlayerComponent) {
        val accDiff = abs(deltaX * deltaTime)
        player.acceleration = mapToRange(accDiff, 0f, 0.1f, 1f, 20f)
    }
}
