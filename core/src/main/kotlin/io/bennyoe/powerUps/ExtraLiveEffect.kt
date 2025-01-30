package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.bennyoe.ecs.components.PlayerComponent
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<ExtraLiveEffect>()

class ExtraLiveEffect : PowerUpEffect {
    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val player = playerEntity[PlayerComponent.mapper]!!
        player.lives++
        LOG.debug { "New Life! Lifes: ${player.lives}" }
    }
}
