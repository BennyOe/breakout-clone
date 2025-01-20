package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import io.bennyoe.UNIT_SCALE
import io.bennyoe.ecs.components.TimerComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.get

class ChangeSizeEffect : PowerUpEffect {

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val playerTransform = playerEntity[TransformComponent.mapper]!!
        playerTransform.size.x = minOf(playerTransform.size.x + 4, 10f)

        playerEntity.add(TimerComponent(5f) {
            playerTransform.size.x = 128 * UNIT_SCALE
        })
    }

}
