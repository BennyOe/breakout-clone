package io.bennyoe.screens

import BearoutMap
import MapEntry
import SelectedBrick
import SelectedPowerUp
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
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
import io.bennyoe.ui.LevelDesignerUi
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.graphics.use
import ktx.log.logger

private val LOG = logger<LevelDesignerScreen>()

class LevelDesignerScreen(
    game: Main,
    private val assets: AssetStorage,
    var bearoutMap: BearoutMap? = null
) : Screen(game) {
    private val startRowIndex = 9
    private val brickAtlas by lazy { assets[TextureAtlasAsset.BRIKCS.descriptor] }
    private val powerUpAtlas by lazy { assets[TextureAtlasAsset.POWERUPS.descriptor] }
    private val shapeRenderer = ShapeRenderer()
    private val cellWidth = 2f / UNIT_SCALE
    private val cellHeight = 1f / UNIT_SCALE
    private val columns = WORLD_WIDTH.toInt() / 2
    private val rows = WORLD_HEIGHT.toInt()
    private var lastXGridCoordinate: Int? = null
    private var lastYGridCoordinate: Int? = null
    private val ui by lazy { LevelDesignerUi(this) }

    private var selectedBrick: SelectedBrick? = null
    private var selectedPowerUp: SelectedPowerUp? = null

    override fun show() {
        if (bearoutMap == null) bearoutMap = BearoutMap(
            name = "",
            author = "",
            difficulty = 0,
            rows = rows,
            columns = columns,
            grid = Array(rows) { Array(columns) { null } }
        )
        val assetRefs = gdxArrayOf(
            TextureAsset.entries.map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.entries.map { assets.loadAsync(it.descriptor) },
        ).flatten()
        KtxAsync.launch {
            assetRefs.joinAll()
            stage.addActor(ui)
        }
        super.show()
    }

    override fun render(delta: Float) {
        handleInput()
        drawGrid()
        if (assets.progress.isFinished) {
            showBrickTypes()
            showPowerUpTypes()
            drawBearoutMap()
        }
        drawSelectedOutline()
        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    private fun drawBearoutMap() {
        bearoutMap!!.grid.forEachIndexed { rowNumber, row ->
            row.forEachIndexed { columnNumber, mapEntry ->
                batch.use(viewport.camera.combined) {
                    if (mapEntry?.type != null && rowNumber >= 9) {
                        it.draw(brickAtlas.findRegion(mapEntry.type?.atlasKey), 2f * columnNumber, rowNumber.toFloat(), 2f, 1f)
                        if (mapEntry.powerUp != null) {
                            it.draw(powerUpAtlas.findRegion(mapEntry.powerUp?.atlasKey), 2f * columnNumber + 0.5f, rowNumber.toFloat(), 1f, 1f)
                        }
                    }
                }
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
        shapeRenderer.use(ShapeRenderer.ShapeType.Filled) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            shapeRenderer.color = Color(.8f, .2f, 1f, 0.4f)
            if (selectedBrick != null) {
                shapeRenderer.rect(selectedBrick!!.column * cellWidth, 0 * cellHeight, cellWidth, cellHeight)
            }
            if (selectedPowerUp != null) {
                shapeRenderer.rect(selectedPowerUp!!.column * cellWidth, 1 * cellHeight, cellWidth, cellHeight)
            }
        }
        Gdx.gl.glLineWidth(3f)
        shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
            shapeRenderer.color = Color(.8f, 1f, 0f, 1f)
            if (selectedBrick != null) {
                shapeRenderer.rect(selectedBrick!!.column * cellWidth, 0 * cellHeight, cellWidth, cellHeight)
            }
            if (selectedPowerUp != null) {
                shapeRenderer.rect(selectedPowerUp!!.column * cellWidth, 1 * cellHeight, cellWidth, cellHeight)
            }
        }
        Gdx.gl.glLineWidth(1f)
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

    fun saveMap() {
        val json = Json()
        val jsonString = json.toJson(bearoutMap)
        val file: FileHandle = Gdx.files.local("levels/${bearoutMap!!.name}.json")
        file.writeString(jsonString, false)
        LOG.info { "Level ${bearoutMap!!.name} saved in ${file.path()}" }
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.removeScreen<LevelDesignerScreen>()
            game.addScreen(MenuScreen(game))
            game.setScreen<MenuScreen>()
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            val worldX = Gdx.input.x.toFloat()
            val worldY = (GAME_HEIGHT - Gdx.input.y.toFloat())

            val gridX = (worldX / cellWidth).toInt()
            val gridY = (worldY / cellHeight).toInt()

            // check that same cell gets not updated
            if (lastXGridCoordinate == gridX && lastYGridCoordinate == gridY) return

            if (gridX in 0 until columns && gridY in 0 until rows) {
                if (gridY == 0) {
                    // brick is selected
                    handleBrickSelection(gridX)
                } else if (gridY == 1) {
                    // powerUp is selected
                    handlePowerUpSelection(gridX)
                } else if (gridY >= startRowIndex) {
                    // cell in grid is selected
                    handleGridPlacement(gridY, gridX)
                }
            }
            lastXGridCoordinate = gridX
            lastYGridCoordinate = gridY
        }
    }

    private fun handleGridPlacement(gridY: Int, gridX: Int) {
        val activeCell = bearoutMap!!.grid[gridY][gridX]
        if (selectedBrick == null && selectedPowerUp == null) {
            when {
                activeCell == null -> return
                else -> setGridField(gridY, gridX, null)
            }
        }
        if (selectedBrick != null && selectedPowerUp == null) {
            if (activeCell == null) setGridField(gridY, gridX, MapEntry(selectedBrick!!.brickType, null))
            if (activeCell?.type == selectedBrick?.brickType) {
                if (activeCell?.powerUp == null) setGridField(gridY, gridX, null)
                else bearoutMap!!.grid[gridY][gridX]?.powerUp = null
            } else bearoutMap!!.grid[gridY][gridX]?.type = selectedBrick?.brickType
        }
        if (selectedBrick != null && selectedPowerUp != null) {
            if (activeCell == null) setGridField(gridY, gridX, MapEntry(selectedBrick?.brickType, selectedPowerUp?.powerUpType))
            if (activeCell?.type == selectedBrick?.brickType) {
                if (activeCell?.powerUp == selectedPowerUp?.powerUpType) setGridField(gridY, gridX, null)
                else bearoutMap!!.grid[gridY][gridX]?.powerUp = selectedPowerUp?.powerUpType
            } else setGridField(gridY, gridX, MapEntry(selectedBrick?.brickType, selectedPowerUp?.powerUpType))
        }
        if (selectedBrick == null && selectedPowerUp != null) {
            if (activeCell == null) return
            if (activeCell.powerUp == selectedPowerUp?.powerUpType) bearoutMap!!.grid[gridY][gridX]?.powerUp = null
            else bearoutMap!!.grid[gridY][gridX]?.powerUp = selectedPowerUp?.powerUpType
        }
        LOG.debug { "Selected Field is: ${bearoutMap!!.grid[gridY][gridX]}" }
    }

    private fun setGridField(gridY: Int, gridX: Int, field: MapEntry?) {
        bearoutMap!!.grid[gridY][gridX] = field
    }

    private fun handlePowerUpSelection(gridX: Int) {
        if (selectedPowerUp?.column == gridX.toFloat()) {
            selectedPowerUp = null
            return
        }
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
        LOG.debug { "selectedPowerUp: ${selectedPowerUp.toString()}" }
    }

    private fun handleBrickSelection(gridX: Int) {
        if (selectedBrick?.column == gridX.toFloat()) {
            selectedBrick = null
            return
        }
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
        LOG.debug { "selectedBrick: ${selectedBrick.toString()}" }
    }

    fun testMap() {
        stage.clear()
        ui.remove()
        game.removeScreen<LevelDesignerScreen>()
        game.addScreen<GameScreen>(GameScreen(game, assets, true, bearoutMap, true))
        game.setScreen<GameScreen>()
    }
}


