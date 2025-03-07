package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PowerUpTextComponent() : Pool.Poolable, Component {
    var powerUpType: PowerUpType = PowerUpType.EXPLODING_BALL
    var animationTime: Float = 0f
    var maxSize: Float = 22f
    var duration: Float = 3.7f

    companion object {
        val mapper = mapperFor<PowerUpTextComponent>()
    }

    override fun reset() {
        powerUpType = PowerUpType.EXPLODING_BALL
        animationTime = 0f
        maxSize = 22f
        duration = 3.7f
    }
}
