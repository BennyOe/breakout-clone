package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.systems.ColorOverlaySystem

abstract class PowerUpEffect {
    abstract val isAdditionalEffect: Boolean
    abstract val powerUpType: PowerUpType
    abstract var remainingTime: Float
    abstract fun apply(playerEntity: Entity, engine: Engine)
    abstract fun deactivate(playerEntity: Entity, engine: Engine)

    fun activateColorOverlay(color: Color, engine: Engine) {
        val overlay = ColorOverlaySystem
        overlay.color = color
        if (!engine.systems.contains(overlay)) {
            engine.addSystem(overlay)
        }
    }

    fun deactivateColorOverlay() {
        val overlay = ColorOverlaySystem
        overlay.color = Color.CLEAR // Setzt die Farbe zurück und entfernt das System
    }
}
