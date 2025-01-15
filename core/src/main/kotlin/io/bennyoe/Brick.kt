package io.bennyoe

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import ktx.graphics.use

const val DESTRUCTION_PROBABILITY = 0.8
const val POWER_UP_PROBABILITY = 0.2


class Brick(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
) {
    var hitpoints: Int = (Math.random() * 4 + 1).toInt()
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()
    private val isDestructible: Boolean = if (Math.random() < DESTRUCTION_PROBABILITY) true else false
    private val hasPowerUp: Boolean = if (Math.random() < POWER_UP_PROBABILITY) true else false
    private val color: Color = if (isDestructible && hasPowerUp) Color.YELLOW else if (isDestructible) Color.CYAN else Color.RED
    private val font = BitmapFont()
    private val batch = SpriteBatch()


    fun getBrick(): Rectangle{
        return Rectangle(x,y,width,height)
    }

    fun hit(){
        hitpoints--
    }

    fun show() {
        shapeRenderer.use(ShapeRenderer.ShapeType.Filled) {
            shapeRenderer.color = color
            shapeRenderer.rect(x, y, width, height)
        }
        shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
            shapeRenderer.color = Color.BLACK
            shapeRenderer.rect(x, y, width, height)
        }
        batch.use {
            font.color = Color.BLACK
            font.draw(batch, " $hitpoints", x + (width /2), y + (height / 2))
        }
    }
}
