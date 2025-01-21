package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class ExplodingComponent : Component {
    var explosionRadius: Float = 3f
    var isExploding: Boolean = false
    companion object {
        val mapper = mapperFor<ExplodingComponent>()
    }
}
