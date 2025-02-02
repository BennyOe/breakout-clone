package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with

class BallSpeedUpEffect(private val audioService: AudioService) : PowerUpEffect() {
    override val isAdditionalEffect = true
    override val powerUpType = PowerUpType.FAST_BALL
    override var remainingTime = 10f

    override fun apply(playerEntity: Entity, engine: Engine) {
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }

        audioService.play(SoundAsset.PU_SPEED_UP)
        engine.entity {
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.FAST_BALL
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }

        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.boost = 0.1f
        }
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) {
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }
        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.boost = 0f
        }
    }
}
