package io.bennyoe

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import ktx.assets.disposeSafely
import ktx.graphics.use
import java.lang.Math.random
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val OFFSET = 3

class Ball(
    private var x: Float = Gdx.graphics.width / 2f,
    private var y: Float = Gdx.graphics.height / 2f,
    private var radius: Float = 10f,
    private var color: Color = getRandomColor(),
    private var angle: Double = 40 + random() * (Math.PI / 2 - 40),
    private var xSpeed: Double = 500.0 * cos(angle).toFloat(),
    private var ySpeed: Double = 500.0 * sin(angle).toFloat(),
    private var acceleration: Float = 1f,
    private var boost: Float = 0f
) {
    private val shapeRenderer = ShapeRenderer()

    fun getBall(): Circle {
        return Circle(x, y, radius)
    }

    fun update(delta: Float, pedal: Pedal, balls: CopyOnWriteArrayList<Ball>) {
        for (otherBall in balls) {
            if (this != otherBall) {
                intersectsBall(otherBall)
            }
        }
        intersectsPedal(pedal)
        checkScreenEdges()
        x += (this.xSpeed * delta * acceleration + boost).toFloat()
        y += (this.ySpeed * delta * acceleration + boost).toFloat()
    }

    fun show() {
        shapeRenderer.use(ShapeType.Filled) {
            shapeRenderer.color = color
            shapeRenderer.circle(x, y, radius)
        }
    }

    fun isAlive(): Boolean {
        return if (y < -10) {
            false
        } else {
            true
        }
    }

    private fun intersectsBall(other: Ball) {
        if (Intersector.overlaps(Circle(x, y, radius), Circle(other.x, other.y, other.radius))) {
            reverseX()
            reverseY()
        }
    }

    private fun checkScreenEdges() {
        // left collision
        if (x <= radius) {
            x = radius
            reverseX()
        }
        // right collision
        if (x >= Gdx.graphics.width - radius) {
            x = Gdx.graphics.width - radius
            reverseX()
        }
        // top collision
        if (y >= Gdx.graphics.height - radius) {
            y = Gdx.graphics.height - radius
            reverseY()
        }
        // bottom collision
//        if (y <= 0 + radius) {
//            y = radius
//            reverseY()
//        }
    }

    fun intersectsBrick(brick: Brick) {
        if (Intersector.overlaps(getBall(), brick.getBrick())) {
            brick.hit()
            if (abs(y - brick.y) > abs(y - (brick.y + brick.height))) { // ball is hitting the brick on the bottom
                y = brick.y + radius + brick.height + 2;
                reverseY();
            } else if (abs(y - brick.y) < abs(y - (brick.y + brick.height))) { // ball is hitting the brick from top
                y = brick.y - radius - 2;
                reverseY();
            } else if (abs(x - brick.x) > abs(x - (brick.x + brick.width))) { // ball is hitting the brick on the right
                x = brick.x + radius + brick.width + 2;
                reverseX();
            } else { // Ball is hitting brick on the left
                x = brick.x - radius - 2;
                reverseX();
            }
        }
    }

    private fun intersectsPedal(pedal: Pedal) {
        if (Intersector.overlaps(Circle(x, y, radius), Rectangle(pedal.x, pedal.y, pedal.width, pedal.height))) {
            y = pedal.y + pedal.height + radius + OFFSET
            val xDiff = x - pedal.x
            val angle = map(xDiff, 0f, pedal.width, Math.toRadians(140.0), Math.toRadians(40.0))
            acceleration = when (pedal.acceleration) {
                Acceleration.SLOW -> acceleration
                Acceleration.MEDIUM -> 1.2f
                Acceleration.FAST -> 1.8f
            }
            xSpeed = 400 * cos(angle) * acceleration
            ySpeed = 400 * sin(angle) * acceleration
        }
    }

    private fun map(value: Float, start1: Float, stop1: Float, start2: Double, stop2: Double): Double {
        return start2 + (value - start1) * (stop2 - start2) / (stop1 - start1)
    }

    private fun reverseX() {
        xSpeed *= -1
    }

    private fun reverseY() {
        ySpeed *= -1
    }

     fun dispose() {
        shapeRenderer.disposeSafely()
    }

    private fun setBackSpeed() {

    }

}

fun getRandomColor(): Color {
    val colors = listOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.ORANGE,
        Color.PURPLE,
        Color.CYAN,
        Color.MAGENTA,
        Color.WHITE,
        Color.BLACK
    )
    return colors[Random.nextInt(colors.size)]
}
