package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
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
    private val viewport: Viewport
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

        // Mausposition transformieren
        val mouseX = Gdx.input.x.toFloat()
        tmpVec.set(mouseX, 0f, 0f) // Nur X-Wert
        viewport.unproject(tmpVec) // Transformiere in Weltkoordinaten

        // Pedal-Begrenzungen berechnen
        pedalMaxPosition.set(Gdx.graphics.width.toFloat(), 0f, 0f) // Breite in Pixel
        viewport.unproject(pedalMaxPosition) // Transformiere Begrenzung in Weltkoordinaten
        pedalMaxPosition.x -= transform.size.x // Abziehen der Breite des Pedals

        if (player.lastXMousePosition == null) {
            player.lastXMousePosition = tmpVec.x
        }

        val mouseDeltaX = tmpVec.x - player.lastXMousePosition!!
        player.lastXMousePosition = tmpVec.x

        // Spielerposition anpassen
        // graphical representation
        transform.interpolatedPosition.x = calculatePlayerPosition(player, mouseDeltaX, transform)
        // collision representation
        transform.position.x = calculatePlayerPosition(player, mouseDeltaX, transform)

        calculateAcceleration(mouseDeltaX, deltaTime, player)
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

    private fun calculateAcceleration(mouseDeltaX: Float, deltaTime: Float, player: PlayerComponent) {
        val accDiff = abs(mouseDeltaX * deltaTime)
        player.acceleration = mapToRange(accDiff, 0f, 0.1f, 1f, 12f)
    }
}
