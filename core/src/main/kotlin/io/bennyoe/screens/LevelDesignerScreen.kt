package io.bennyoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.GAME_WIDTH
import io.bennyoe.Main
import io.bennyoe.UNIT_SCALE
import io.bennyoe.assets.TextureAsset
import io.bennyoe.assets.TextureAtlasAsset
import io.bennyoe.ecs.systems.BrickType
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.graphics.use
import ktx.log.logger

private val LOG = logger<LevelDesignerScreen>()

class LevelDesignerScreen(game: Main, private val assets: AssetStorage) : Screen(game) {
    private val brickAtlas by lazy { assets[TextureAtlasAsset.BRIKCS.descriptor] }
    private val powerUpAtlas by lazy { assets[TextureAtlasAsset.POWERUPS.descriptor] }
    private val shapeRenderer = ShapeRenderer()
    private val cellWidth = 2f / UNIT_SCALE
    private val cellHeight = 1f / UNIT_SCALE
    private val columns = GAME_WIDTH.toInt() / 2
    private val rows = GAME_HEIGHT.toInt()

    private val levelGrid = Array(columns) { Array(rows) { BrickType.BLUE } }
    private var selectedBrick: BrickType? = null

    override fun show() {
        val assetRefs = gdxArrayOf(
            TextureAsset.entries.map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.entries.map { assets.loadAsync(it.descriptor) },
        ).flatten()
        KtxAsync.launch {
            assetRefs.joinAll()
        }
        super.show()
    }

    override fun render(delta: Float) {
        handleInput()
        drawGrid()
        if (assets.progress.isFinished) {
            showBrickTypes()
        }
    }

    private fun handleInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            val worldX = Gdx.input.x.toFloat() / UNIT_SCALE
            val worldY = (GAME_HEIGHT - Gdx.input.y.toFloat()) / UNIT_SCALE

            val gridX = (worldX / cellWidth).toInt()
            val gridY = (worldY / cellHeight).toInt()

            if (gridX in 0 until columns && gridY in 0 until rows) {
                levelGrid[gridX][gridY] = selectedBrick ?: BrickType.BLUE
            }
            LOG.debug { selectedBrick.toString() }
        }
    }

    private fun drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GRAY
        for (x in 0 until columns) {
            for (y in 9 until rows) {
                shapeRenderer.rect(x * cellWidth, y * cellHeight, cellWidth, cellHeight)
            }
        }
        shapeRenderer.end()
    }

    private fun showBrickTypes() {
        var index = 0
        BrickType.entries.forEach { brickType ->
            batch.use(viewport.camera.combined) {
                it.draw(brickAtlas.findRegion(brickType.atlasKey), 2f * index, 0f, 2f, 1f)
            }
            index++
        }
    }
}
