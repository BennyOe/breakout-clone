package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with

class BallSpeedUpEffect : PowerUpEffect {

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }

        engine.entity {
            with<PowerUpTextComponent>{
                powerUpType = PowerUpType.FAST_BALL
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }

        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.boost = 0.1f
            ball.addEffectTimer("BallSpeedUp", 5f) {
                ballComponent.boost = 0f
            }
        }
    }
}
