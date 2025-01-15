package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor
import java.lang.Math.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BallComponent : Component{
    var angle = 40 + random() * (PI / 2 - 40)
    var xSpeed = 5 * cos(angle).toFloat()
    var ySpeed = 5 * sin(angle).toFloat()
    var acceleration = 1f
    var boost = 0f

    companion object {
        val mapper = mapperFor<BallComponent>()
    }
}
