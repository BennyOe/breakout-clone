package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import java.lang.Math.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BallComponent : Component, Pool.Poolable {
    var angle = 40 + random() * (PI / 2 - 40)
    var xSpeed = 10 * cos(angle).toFloat()
    var ySpeed = 10 * sin(angle).toFloat()
    var acceleration = 1f
    var boost = 0f
    var isPenetrating = false
    var isExploding = false
    var isSticky = false
    var offsetXToPlayer = 0f

    override fun reset() {
        angle = 40 + random() * (PI / 2 - 40)
        xSpeed = 10 * cos(angle).toFloat()
        ySpeed = 10 * sin(angle).toFloat()
        acceleration = 1f
        boost = 0f
        isPenetrating = false
        isExploding = false
        isSticky = false
        offsetXToPlayer = 0f
    }

    companion object {
        val mapper = mapperFor<BallComponent>()
    }

}
