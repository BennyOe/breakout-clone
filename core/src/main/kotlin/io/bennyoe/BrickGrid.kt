package io.bennyoe

import com.badlogic.gdx.Gdx
import java.util.concurrent.CopyOnWriteArrayList

const val ROW_HEIGHT = 32f
const val ROWS = 15

class BrickGrid(
) {
    private val columnWidth = Gdx.graphics.width / 20f
    private val bricksPerColumn = Gdx.graphics.width / columnWidth.toInt()
    private val screenTop = Gdx.graphics.height.toFloat()
    private val brickGrid = CopyOnWriteArrayList<Brick>()

    fun initialize() {
        for (rowCount in 1..ROWS) {
            for (columnCount in 0..bricksPerColumn) {
                val rand = Math.random()
                if (rand >= 0.5) {
                    brickGrid.add(Brick(columnCount * columnWidth, screenTop - (rowCount * ROW_HEIGHT), columnWidth, ROW_HEIGHT))
                }
            }
        }
    }

    fun updateBricks(balls: MutableList<Ball>) {
        for (brick in brickGrid) {
            for (ball in balls) {
                ball.intersectsBrick(brick)
            }
            if (brick.hitpoints <= 0) {
                brickGrid.remove(brick)
            }
        }
    }

    fun show() {
        for (brick in brickGrid) brick.show()
    }
}
