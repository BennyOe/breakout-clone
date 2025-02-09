package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.Main
import io.bennyoe.UNIT_SCALE
import io.bennyoe.assets.SoundAsset
import io.bennyoe.assets.TextureAtlasAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.PowerUpComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.powerUps.PowerUpEffect
import io.bennyoe.screens.GameOverScreen
import io.bennyoe.screens.GameScreen
import io.bennyoe.screens.GameWinScreen
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.getSystem
import ktx.ashley.with
import ktx.log.logger
import java.util.concurrent.CopyOnWriteArrayList

private val LOG = logger<GameStateSystem>()

class GameStateSystem(
    private val audioService: AudioService,
    private val game: Main,
    private val ballsAtlas: TextureAtlas,
    private var _score: Int = 0
) :
    EntitySystem() {
    private val playerAtlas by lazy { game.assets[TextureAtlasAsset.PLAYER.descriptor] }
    private val bricksAtlas by lazy { game.assets[TextureAtlasAsset.BRIKCS.descriptor] }
    private val activePowerUps = CopyOnWriteArrayList<PowerUpEffect>()
    private var activeMainPowerUp: PowerUpEffect? = null
    val score: Int get() = _score
    var scoreMultiplier = 1

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        initializeGame()
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val player = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).first()
        val playerComponent = player[PlayerComponent.mapper]!!
        val playerTransformComponent = player[TransformComponent.mapper]!!
        if (playerComponent.lives <= 0) gameOver()
        checkForBallsInGame(playerComponent, playerTransformComponent)
        if (allBricksDestroyed()) gameWin()

        activePowerUps.forEach { activePowerUp ->
            if (activePowerUp.remainingTime <= 0) {
                activePowerUp.deactivate(player, engine)
                activePowerUps.remove(activePowerUp)
                if (activeMainPowerUp == activePowerUp) activeMainPowerUp = null
            }
            activePowerUp.remainingTime -= deltaTime
        }
    }

    private fun allBricksDestroyed(): Boolean {
        return engine.getEntitiesFor(Family.all(BrickComponent::class.java).get()).size() == 0
    }

    fun addScore(addedValue: Int) {
        _score += addedValue
    }

    fun isMainPowerUpTypeActive(powerUpType: PowerUpType): Boolean {
        if (activeMainPowerUp == null) return false
        return activeMainPowerUp?.powerUpType == powerUpType
    }

    fun activatePowerUp(powerUp: PowerUpEffect) {
        val playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).first()
        if (!powerUp.isAdditionalEffect && activeMainPowerUp?.powerUpType != powerUp.powerUpType) {
            activeMainPowerUp?.deactivate(playerEntity, engine)
            activeMainPowerUp = powerUp
            activePowerUps.remove(powerUp)
        }
        powerUp.apply(playerEntity, engine)
        val activePU = activePowerUps.firstOrNull { it.powerUpType == powerUp.powerUpType }
        if (activePU != null) {
            activePU.remainingTime += 5f
        } else {
            activePowerUps.add(powerUp)
        }
    }

    private fun initializeGame() {
        val brickSystem = engine.getSystem<BrickSystem>()
        brickSystem.initializeBricks(bricksAtlas)
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(1f, 1f, 0f)
                size.set(128 * UNIT_SCALE, 32 * UNIT_SCALE)
            }
            with<GraphicComponent> {
                sprite.run {
                    setRegion(playerAtlas.findRegion("bear"))
                    setOriginCenter()
                }
            }
            with<PlayerComponent>()
        }

        val middleOfPlayer = 1f + (128 * UNIT_SCALE / 2 - 32 * UNIT_SCALE / 2)
        createNewBall(middleOfPlayer)
    }

    private fun resetPowerUps() {
        val player = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).first()
        activePowerUps.forEach {
            it.deactivate(player, engine)
        }
        activePowerUps.clear()
        activeMainPowerUp = null
    }

    private fun checkForBallsInGame(playerComponent: PlayerComponent, playerTransformComponent: TransformComponent) {
        val balls = engine.getEntitiesFor(Family.all(BallComponent::class.java).get())
        if (balls.size() <= 0) {
            playerComponent.lives--
            resetRound(playerTransformComponent)
        }
    }

    private fun resetRound(playerTransformComponent: TransformComponent) {
        LOG.debug { "New Round started" }
        if (score >= 20) addScore(-20) else addScore(-score)
        audioService.play(SoundAsset.BALL_LOST)
        resetPowerUps()
        engine.removeAllEntities(Family.all(PowerUpComponent::class.java).get())
        val middleOfPlayer = playerTransformComponent.position.x + (128 * UNIT_SCALE / 2 - 32 * UNIT_SCALE / 2)
        createNewBall(middleOfPlayer)
    }

    private fun gameOver() {
        LOG.debug { "GAME OVER" }
        resetPowerUps()
        engine.removeAllEntities()
        audioService.stop(true)
        game.removeScreen<GameScreen>()
        game.addScreen(GameOverScreen(game, score))
        game.setScreen<GameOverScreen>()
    }

    private fun gameWin() {
        LOG.debug { "GAME WIN" }
        resetPowerUps()
        engine.removeAllEntities()
        audioService.stop(true)
        game.removeScreen<GameScreen>()
        game.addScreen(GameWinScreen(game, score))
        game.setScreen<GameWinScreen>()
    }

    private fun createNewBall(positionX: Float) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(positionX, 1f, 0f)
                size.set(32 * UNIT_SCALE, 32 * UNIT_SCALE)
            }
            with<GraphicComponent> {
                sprite.run {
                    setRegion(ballsAtlas.findRegion("Ball_Yellow_Glossy_trans-32x32"))
                    setOriginCenter()
                }
            }
            with<BallComponent>() {
                acceleration = 1f
                isSticky = true
                isPenetrating = false
                isExploding = false
            }
        }
    }
}
