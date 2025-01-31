package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.assets.MusicAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.ecs.systems.ShapeRenderingSystem
import io.bennyoe.ecs.systems.ShooterSystem
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with

class ShooterEffect(private val audioService: AudioService) : PowerUpEffect {
    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        engine.entity {
            with<PowerUpTextComponent>{
                powerUpType = PowerUpType.SHOOTER
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        val graphics = playerEntity[GraphicComponent.mapper]!!

        audioService.play(MusicAsset.SHOOTER_MUSIC, 1f, true)
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("bear_shooter"))
        }

        val shooterSystem = ShooterSystem(audioService)
        val colorOverlaySystem = ShapeRenderingSystem(Color(1f, 0f, 0f, 0.1f))
        engine.addSystem(colorOverlaySystem)
        engine.addSystem(shooterSystem)
        ballEntity.addEffectTimer("SHOOTER", 5f) {
            audioService.play(MusicAsset.BG_MUSIC, 0.25f)
            engine.removeSystem(shooterSystem)
            engine.removeSystem(colorOverlaySystem)
            graphics.sprite.run {
                setRegion(playerAtlas.findRegion("bear"))
            }
        }
    }
}
