package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class TimerComponent(
    var timer: Float,
    var onExpire: () -> Unit
) : Component {
    companion object {
        val mapper = mapperFor<TimerComponent>()
    }
}
