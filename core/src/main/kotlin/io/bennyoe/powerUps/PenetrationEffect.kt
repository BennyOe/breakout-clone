package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
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

class PenetrationEffect(private val audioService: AudioService) : PowerUpEffect() {
    override val isAdditionalEffect = false
    override val powerUpType = PowerUpType.PENETRATION
    override var remainingTime = 5f

    override fun apply(playerEntity: Entity, engine: Engine) {
        audioService.play(SoundAsset.PU_PENETRATION)
        engine.entity {
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.PENETRATION
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }

        activateColorOverlay(Color(0f, 1f, 0f, 0.1f), engine)
        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.isPenetrating = true
        }
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) {
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }
        deactivateColorOverlay()
        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.isPenetrating = false
        }
    }
}
