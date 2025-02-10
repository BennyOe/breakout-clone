package io.bennyoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Json
import io.bennyoe.GAME_HEIGHT
import io.bennyoe.Main
import io.bennyoe.UNIT_SCALE
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.assets.TextureAsset
import io.bennyoe.assets.TextureAtlasAsset
import io.bennyoe.ecs.components.PowerUpType
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
    private val startRowIndex = 9
    private val brickAtlas by lazy { assets[TextureAtlasAsset.BRIKCS.descriptor] }
    private val powerUpAtlas by lazy { assets[TextureAtlasAsset.POWERUPS.descriptor] }
    private val shapeRenderer = ShapeRenderer()
    private val cellWidth = 2f / UNIT_SCALE
    private val cellHeight = 1f / UNIT_SCALE
    private val columns = WORLD_WIDTH.toInt() / 2
    private val rows = WORLD_HEIGHT.toInt()

    private val bearoutMap = BearoutMap("testMap", rows, columns, Array(rows) { Array(columns) { MapEntry(null, null) } })
    private var selectedBrick: SelectedBrick? = null
    private var selectedPowerUp: SelectedPowerUp? = null

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
        drawSelectedOutline()
        if (assets.progress.isFinished) {
            showBrickTypes()
            showPowerUpTypes()
            drawBearoutMap()
        }
    }

    private fun drawBearoutMap() {
        bearoutMap.grid.forEachIndexed { rowNumber, row ->
            row.forEachIndexed { columnNumber, mapEntry ->
                batch.use(viewport.camera.combined) {
                    if (mapEntry?.type != null && rowNumber >= 9) {
                        it.draw(brickAtlas.findRegion(mapEntry.type.atlasKey), 2f * columnNumber, rowNumber.toFloat(), 2f, 1f)
                        if (mapEntry.powerUp != null) {
                            it.draw(powerUpAtlas.findRegion(mapEntry.powerUp.atlasKey), 2f * columnNumber + 0.5f, rowNumber.toFloat(), 1f, 1f)
                        }
                    }
                }
            }
        }
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Keys.S)){
            saveMap()
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            val worldX = Gdx.input.x.toFloat()
            val worldY = (GAME_HEIGHT - Gdx.input.y.toFloat())

            val gridX = (worldX / cellWidth).toInt()
            val gridY = (worldY / cellHeight).toInt()
            LOG.debug { "WorldX: $worldX WorldY: $worldY GridX: $gridX GridY: $gridY" }

            if (gridX in 0 until columns && gridY in 0 until rows) {
                if (gridY == 0) {
                    selectedBrick = when (gridX) {
                        0 -> SelectedBrick(0f, BrickType.BLUE)
                        1 -> SelectedBrick(1f, BrickType.YELLOW)
                        2 -> SelectedBrick(2f, BrickType.GREEN)
                        3 -> SelectedBrick(3f, BrickType.ORANGE)
                        4 -> SelectedBrick(4f, BrickType.RED)
                        5 -> SelectedBrick(5f, BrickType.PURPLE)
                        6 -> SelectedBrick(6f, BrickType.STONE)
                        else -> null
                    }
                } else if (gridY == 1) {
                    selectedPowerUp = when (gridX) {
                        0 -> SelectedPowerUp(0f, PowerUpType.CHANGE_SIZE)
                        1 -> SelectedPowerUp(1f, PowerUpType.EXPLODING_BALL)
                        2 -> SelectedPowerUp(2f, PowerUpType.STICKY_BALL)
                        3 -> SelectedPowerUp(3f, PowerUpType.PENETRATION)
                        4 -> SelectedPowerUp(4f, PowerUpType.SHOOTER)
                        5 -> SelectedPowerUp(5f, PowerUpType.FAST_BALL)
                        6 -> SelectedPowerUp(6f, PowerUpType.REVERSE_CONTROL)
                        7 -> SelectedPowerUp(7f, PowerUpType.MULTIBALL)
                        8 -> SelectedPowerUp(8f, PowerUpType.BONUS_HEART)
                        9 -> SelectedPowerUp(9f, PowerUpType.SHEEP)
                        else -> null
                    }
                    LOG.debug { selectedBrick.toString() }
                    LOG.debug { selectedPowerUp.toString() }
                } else if (gridY >= startRowIndex) {
                    bearoutMap.grid[gridY][gridX] = MapEntry(
                        type = selectedBrick?.brickType,
                        powerUp = if (selectedBrick != null) selectedPowerUp?.powerUpType
                        else null
                    )
                }
                LOG.debug { "Selected Field is: ${bearoutMap.grid[gridY][gridX]}" }
            }
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

    private fun drawSelectedOutline() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.RED
        if (selectedBrick != null) {
            shapeRenderer.rect(selectedBrick!!.column * cellWidth, 0 * cellHeight, cellWidth, cellHeight)
        }
        if (selectedPowerUp != null) {
            shapeRenderer.rect(selectedPowerUp!!.column * cellWidth, 1 * cellHeight, cellWidth, cellHeight)
        }
        shapeRenderer.end()
    }

    private fun showBrickTypes() {
        BrickType.entries.forEachIndexed { index, brickType ->
            batch.use(viewport.camera.combined) {
                it.draw(brickAtlas.findRegion(brickType.atlasKey), 2f * index, 0f, 2f, 1f)
            }
        }
    }

    private fun showPowerUpTypes() {
        PowerUpType.entries.forEachIndexed { index, powerUpType ->
            batch.use(viewport.camera.combined) {
                it.draw(powerUpAtlas.findRegion(powerUpType.atlasKey), 2f * index + 0.5f, 1f, 1f, 1f)
            }
        }
    }
    private fun saveMap() {
        val json = Json()
        val jsonString = json.toJson(bearoutMap)
        val file: FileHandle = Gdx.files.local("levels/${bearoutMap.name}.json")
        file.writeString(jsonString, false)
        LOG.info { "Level ${bearoutMap.name} saved in ${file.path()}" }
    }
}

data class BearoutMap(
    val name: String,
    val rows: Int,
    val columns: Int,
    val grid: Array<Array<MapEntry?>> = Array(rows) { Array(columns) { null } }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BearoutMap

        if (rows != other.rows) return false
        if (columns != other.columns) return false
        if (name != other.name) return false
        if (!grid.contentDeepEquals(other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + columns
        result = 31 * result + name.hashCode()
        result = 31 * result + grid.contentDeepHashCode()
        return result
    }
}

data class MapEntry(
    val type: BrickType?,
    val powerUp: PowerUpType?
)

data class SelectedBrick(
    val column: Float,
    val brickType: BrickType
)

data class SelectedPowerUp(
    val column: Float,
    val powerUpType: PowerUpType
)
