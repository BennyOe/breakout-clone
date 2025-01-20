package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class PowerUpComponent(
) : Component {
    var powerUpType: PowerUpType = PowerUpType.SHOOTER

    companion object {
        val mapper = mapperFor<PowerUpComponent>()
    }
}

enum class PowerUpType {
    CHANGE_SIZE, EXPLODING_BALL, STICKY_BALL, PENETRATION, SHOOTER, FAST_BALL, REVERSE_CONTROL, MULTIBALL, BONUS_HEART
}
