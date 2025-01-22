package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PlayerComponent : Component, Pool.Poolable {
    var lives = 5
    var score = 0
    var name = ""
    var acceleration = 1f
    var isReversed = false
    var isSticky = false
    var lastXMousePosition: Float? = null

    override fun reset() {
        lives = 5
        score = 0
        name = ""
        acceleration = 1f
        isReversed = false
        isSticky = false
        lastXMousePosition = null
    }

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }
}
