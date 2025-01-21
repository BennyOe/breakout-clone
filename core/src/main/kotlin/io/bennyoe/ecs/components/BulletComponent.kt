package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.ashley.mapperFor

class BulletComponent : Component{
    val shapeRenderer = ShapeRenderer()

    companion object {
        val mapper = mapperFor<BulletComponent>()
    }
}
