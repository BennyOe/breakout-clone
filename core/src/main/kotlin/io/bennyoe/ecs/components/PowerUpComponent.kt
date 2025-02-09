package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class PowerUpComponent() : Component {
    var powerUpType: PowerUpType = PowerUpType.SHOOTER

    companion object {
        val mapper = mapperFor<PowerUpComponent>()
    }
}

enum class PowerUpType(val type: String, val atlasKey: String) {
    CHANGE_SIZE("change-size", "pizza_trans"),
    EXPLODING_BALL("explosion", "bomb_trans"),
    STICKY_BALL("sticky", "honeypot_trans"),
    PENETRATION("penetration", "muscle_trans"),
    SHOOTER("shooter", "flint_pxl_sml"),
    FAST_BALL("speed-up", "coffee_mug"),
    REVERSE_CONTROL("reverse", "confused_trans"),
    MULTIBALL("multiball", "multiball_rounded"),
    BONUS_HEART("heart", "heart"),
    SHEEP("sheep", "sheep")
}
