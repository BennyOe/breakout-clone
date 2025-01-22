package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.get

class StickyEffect : PowerUpEffect {
    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val player = playerEntity[PlayerComponent.mapper]!!
        player.isSticky = true

        playerEntity.addEffectTimer("STICKY", 5f) {
            player.isSticky = false
        }
    }
}
