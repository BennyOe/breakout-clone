package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PlayerComponent : Component, Pool.Poolable {
    var lives = 5
    var name = ""
    var isReversed = false
    var isSticky = false
    var lastXMousePosition: Float? = null
    var lastXPosition: Float? = null
    var speed: Float = 0f
    var maxSpeed: Float = 30f
    var acceleration: Float = 0.6f
    var deceleration: Float = 0.5f

    override fun reset() {
        lives = 5
        name = ""
        isReversed = false
        isSticky = false
        lastXMousePosition = null
        speed = 0f
        maxSpeed = 30f
        acceleration = 0.6f
        deceleration = 0.5f
    }

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }
}
