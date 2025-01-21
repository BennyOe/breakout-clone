package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class TimerComponent : Component {
    val timers = mutableMapOf<String, TimerData>()

    companion object {
        val mapper = mapperFor<TimerComponent>()
    }

    data class TimerData(var duration: Float, val onExpire: () -> Unit)
}

