package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.get

class ExplodingEffect : PowerUpEffect {
    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }

        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.isExploding = true
            ball.addEffectTimer("Exploding", 5f) {
                ballComponent.isExploding = false
            }
        }
    }
}
