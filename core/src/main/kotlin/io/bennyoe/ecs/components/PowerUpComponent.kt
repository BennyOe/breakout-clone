package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class PowerUpComponent() : Component {
    var powerUpType: PowerUpType = PowerUpType.SHOOTER

    companion object {
        val mapper = mapperFor<PowerUpComponent>()
    }
}

enum class PowerUpType(val type: String) {
    CHANGE_SIZE("change-size"),
    EXPLODING_BALL("explosion"),
    STICKY_BALL("sticky"),
    PENETRATION("penetration"),
    SHOOTER("shooter"),
    FAST_BALL("speed-up"),
    REVERSE_CONTROL("reverse"),
    MULTIBALL("multiball"),
    BONUS_HEART("heart")
}
