package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.TimerComponent
import ktx.ashley.get

class BallSpeedUpEffect : PowerUpEffect {

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }

        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.boost = 0.1f
            ball.add(TimerComponent(5f) {
                ballComponent.boost = 0f
            })
        }
    }
}
