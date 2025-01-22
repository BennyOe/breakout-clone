package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class BulletComponent : Component{

    companion object {
        val mapper = mapperFor<BulletComponent>()
    }
}
