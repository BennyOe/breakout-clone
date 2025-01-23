package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TransformComponent : Component, Pool.Poolable {
    val position = Vector3(0f, 0f, 0f)
    val prevPosition = Vector3(0f, 0f, 0f)
    val interpolatedPosition = Vector3(0f, 0f, 0f)
    val size = Vector2(1f, 1f)

    override fun reset() {
        position.set(0f, 0f, 0f)
        size.set(1f, 1f)
        prevPosition.set(0f, 0f, 0f)
        interpolatedPosition.set(0f, 0f, 0f)
    }

    fun setInitialPosition(x: Float, y: Float, z: Float) {
        position.set(x, y, z)
        prevPosition.set(x, y, z)
        interpolatedPosition.set(x, y, z)
    }

    companion object {
        val mapper = mapperFor<TransformComponent>()
    }

}
