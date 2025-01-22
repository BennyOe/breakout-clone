package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import java.lang.Math.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BallComponent : Component{
    var angle = 40 + random() * (PI / 2 - 40)
    var xSpeed = 10 * cos(angle).toFloat()
    var ySpeed = 10 * sin(angle).toFloat()
    var acceleration = 1f
    var boost = 0f
    var isPenetrating = false
    var isExploding = false
    var isSticky = false
    var offsetXToPlayer = 0f

    companion object {
        val mapper = mapperFor<BallComponent>()
    }
}
