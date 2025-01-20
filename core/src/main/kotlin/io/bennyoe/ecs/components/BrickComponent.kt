package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class BrickComponent : Component {
    var hitpoints: Int = 1
    var isDestructible = true
    var hasPowerUp = true
    var powerUpType = PowerUpType.SHOOTER
    var type = BrickType.DEFAULT

    companion object {
        val mapper = mapperFor<BrickComponent>()
    }
}


enum class BrickType {
    DEFAULT
}
