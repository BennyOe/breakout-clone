package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.systems.ShapeRenderingSystem
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.get

class PenetrationEffect : PowerUpEffect {
    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }

        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.isPenetrating = true
            val colorOverlaySystem = ShapeRenderingSystem(Color(0f, 1f, 0f, 0.1f))
            engine.addSystem(colorOverlaySystem)
            ball.addEffectTimer("Penetration", 5f) {
                ballComponent.isPenetrating = false
                engine.removeSystem(colorOverlaySystem)
            }
        }
    }
}
