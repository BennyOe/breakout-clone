package io.bennyoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.Ball
import io.bennyoe.BrickGrid
import io.bennyoe.Main
import io.bennyoe.Pedal
import ktx.assets.disposeSafely
import ktx.graphics.use
import java.util.concurrent.CopyOnWriteArrayList

const val BALLS = 8

class BreakoutScreen(game: Main) : Screen(game) {
    private val shapeRenderer = ShapeRenderer()
    private val balls = CopyOnWriteArrayList<Ball>()
    private val pedal = Pedal(Gdx.graphics.width.toFloat() / 2, 200f)
    private val brickGrid = BrickGrid()
    private val font = BitmapFont()

    override fun show() {
        brickGrid.initialize()
        for (i in 1..BALLS) {
            balls.add(Ball(Math.random().toFloat() * 300, Math.random().toFloat() * 300))
        }
    }

    override fun render(delta: Float) {
        showFps()
        brickGrid.updateBricks(balls)
        brickGrid.show()
        pedal.update()
        pedal.show()
        balls.forEach { ball ->
            ball.update(delta, pedal, balls)
            if (ball.isAlive()){
                ball.show()
            } else {
                balls.remove(ball)
                ball.dispose()
            }
        }
    }

    private fun showFps() {
        batch.use(viewport.camera.combined) {
            font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", 0f, 0f)
            font.draw(batch, "Balls: ${balls.size}", 0f, 1f)
        }
    }

    override fun dispose() {
        shapeRenderer.disposeSafely()
    }
}
