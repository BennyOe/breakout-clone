package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.log.logger

private val LOG = logger<ExtraLiveEffect>()

class ExtraLiveEffect(private val audioService: AudioService) : PowerUpEffect() {
    override val isAdditionalEffect = true
    override val powerUpType = PowerUpType.BONUS_HEART
    override var remainingTime = 5f

    override fun apply(playerEntity: Entity, engine: Engine) {
        audioService.play(SoundAsset.PU_HEART)
        engine.entity{
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.BONUS_HEART
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        val player = playerEntity[PlayerComponent.mapper]!!
        if (player.lives < 5) {
            player.lives++
            LOG.debug { "New Life! Lifes: ${player.lives}" }
        } else {
            LOG.debug { "Max lives reached! Current lives: ${player.lives}" }
        }
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) = Unit
}
