package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.assets.MusicAsset
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.ecs.systems.GameStateSystem
import io.bennyoe.ecs.systems.ShooterSystem
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.getSystem
import ktx.ashley.with

class ShooterEffect(private val audioService: AudioService) : PowerUpEffect() {
    override val isAdditionalEffect = false
    override val powerUpType = PowerUpType.SHOOTER
    override var remainingTime = 5f

    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }
    private val shooterSystem by lazy { ShooterSystem }

    override fun apply(playerEntity: Entity, engine: Engine) {
        audioService.play(SoundAsset.PU_SHOOTER)
        shooterSystem.init(audioService)
        val gameStateSystem = engine.getSystem<GameStateSystem>()
        engine.entity {
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.SHOOTER
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        val graphics = playerEntity[GraphicComponent.mapper]!!

        if (!audioService.isMusicTypePlaying(MusicAsset.SHOOTER_MUSIC)) {
            audioService.play(MusicAsset.SHOOTER_MUSIC, 0.7f)
            graphics.sprite.run {
                setRegion(playerAtlas.findRegion("bear_shooter"))
            }
        }
        activateColorOverlay(Color(1f, 0f, 0f, 0.1f), engine)
        engine.addSystem(shooterSystem)
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) {
        val graphics = playerEntity[GraphicComponent.mapper]!!
        audioService.play(MusicAsset.BG_MUSIC, 0.25f)
        engine.removeSystem(shooterSystem)
        deactivateColorOverlay()
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("bear"))
        }

    }
}
