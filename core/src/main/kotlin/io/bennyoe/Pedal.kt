package io.bennyoe

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.graphics.use
import kotlin.math.abs

class Pedal(
    var x: Float,
    var width: Float,
    val height: Float = 16f,
    val y: Float = 30f,
    var acceleration: Acceleration = Acceleration.MEDIUM,
) {
    private val pedalTexture = Texture("pedal2.png")
    private val spriteBatch = SpriteBatch()

    fun update() {
        val accDiff = abs(Gdx.input.x - x)
        acceleration = when {
            accDiff < 1 -> Acceleration.SLOW
            accDiff > 10 -> Acceleration.FAST
            else -> Acceleration.MEDIUM
        }
        x = setXPosition()
    }

    fun show() {
        spriteBatch.use {
            spriteBatch.draw(pedalTexture, x, y)
        }
    }

    private fun setXPosition(): Float {
        val mouseX = Gdx.input.x.toFloat()
        val pedalMaxPosition = Gdx.graphics.width - width
        return when {
            mouseX < 0 -> 0f
            mouseX > pedalMaxPosition -> pedalMaxPosition
            else -> mouseX
        }
    }
}

enum class Acceleration {
    SLOW, MEDIUM, FAST
}
