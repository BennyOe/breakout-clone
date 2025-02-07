package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.UNIT_SCALE
import io.bennyoe.assets.MusicAsset
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.ecs.systems.GameStateSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.getSystem
import ktx.ashley.with

class SheepEffect(private val audioService: AudioService, private val engine: Engine) : PowerUpEffect() {
    override val isAdditionalEffect = false
    override val powerUpType = PowerUpType.SHEEP
    override var remainingTime = 10f
    private val gameStateSystem by lazy { engine.getSystem<GameStateSystem>() }
    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }
    private var colorJob: Job? = null

    override fun apply(playerEntity: Entity, engine: Engine) {

        audioService.play(SoundAsset.PU_SHEEP)
        engine.entity {
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.SHEEP
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        gameStateSystem.scoreMultiplier = 2

        val playerTransform = playerEntity[TransformComponent.mapper]!!
        playerTransform.size.x = 64 * UNIT_SCALE

        val graphics = playerEntity[GraphicComponent.mapper]!!

        if (!gameStateSystem.isMainPowerUpTypeActive(this.powerUpType)) {
            audioService.play(MusicAsset.SHEEP_MUSIC, 1f)
            graphics.sprite.run {
                setRegion(playerAtlas.findRegion("sheep"))
            }
        }
        startColorChange(engine)
    }

    private fun startColorChange(engine: Engine) {
        colorJob?.cancel()

        colorJob = CoroutineScope(Dispatchers.Default).launch {
            val colors = listOf(
                Color(1f, 0f, 0f, 0.1f),
                Color(0f, 1f, 0f, 0.1f),
                Color(0f, 0f, 1f, 0.1f),
                Color(1f, 1f, 0f, 0.1f),
                Color(0.5f, 0f, 0.5f, 0.1f),
                Color(0f, 1f, 1f, 0.1f)
            )
            var index = 0

            while (isActive) {
                activateColorOverlay(colors[index], engine)
                index = (index + 1) % colors.size
                delay(800)
            }
        }
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) {
        colorJob?.cancel()

        val graphics = playerEntity[GraphicComponent.mapper]!!
        val playerTransform = playerEntity[TransformComponent.mapper]!!

        gameStateSystem.scoreMultiplier = 1

        audioService.play(MusicAsset.BG_MUSIC, 0.25f)
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("bear"))
        }
        playerTransform.size.x = 128 * UNIT_SCALE
    }
}
