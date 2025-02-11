package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import io.bennyoe.ecs.systems.BrickType
import ktx.ashley.mapperFor

class BrickComponent : Component {
    var hasPowerUp = true
    var powerUpType: PowerUpType? = null
    var type = BrickType.BLUE
    var hitPoints: Int = type.hitPoints

    companion object {
        val mapper = mapperFor<BrickComponent>()
    }
}
