package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<PlayerInputSystem>()

class PlayerInputSystem(
    private val viewport: Viewport
) : IteratingSystem(
    allOf(PlayerComponent::class).get()
) {
    private val tmpVec = Vector2()
    private val pedalMaxPosition = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val player = entity[PlayerComponent.mapper]
        require(player != null) { "entity has no player entity" }

        val mouseX = Gdx.input.x.toFloat()
        tmpVec.x = mouseX.coerceIn(0f, Gdx.graphics.width.toFloat())

        viewport.unproject(tmpVec) // project pixel to world unit (wu)

        pedalMaxPosition.x = Gdx.graphics.width.toFloat() // get width of window
        viewport.unproject(pedalMaxPosition) // project pixel to wu
        pedalMaxPosition.x -= transform.size.x // subtract the pedal width in wu

        val accDiff = (tmpVec.x - transform.position.x).coerceIn(0f, 50f)

        transform.position.x = when {
            tmpVec.x < 0 -> 0f
            tmpVec.x >= pedalMaxPosition.x -> pedalMaxPosition.x
            else -> tmpVec.x
        }

        player.acceleration = map(accDiff, 0f, 50f, 1f, 8f)
    }

    private fun map(value: Float, orgStart: Float, orgStop: Float, targetStart: Float, targetStop: Float): Float {
        return targetStart + (value - orgStart) * (targetStop - targetStart) / (orgStop - orgStart)
    }
}
