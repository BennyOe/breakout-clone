package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with

class ReverseControlsEffect(private val audioService: AudioService) : PowerUpEffect() {
    override val isAdditionalEffect = true
    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }
    override val powerUpType = PowerUpType.REVERSE_CONTROL
    override var remainingTime = 5f

    override fun apply(playerEntity: Entity, engine: Engine) {
        audioService.play(SoundAsset.PU_REVERSE)
        engine.entity {
            with<PowerUpTextComponent>{
                powerUpType = PowerUpType.REVERSE_CONTROL
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        val player = playerEntity[PlayerComponent.mapper]!!
        player.isReversed = true

        val graphics = playerEntity[GraphicComponent.mapper]!!

        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("reversed-controlls"))
        }
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) {
        val player = playerEntity[PlayerComponent.mapper]!!
        val graphics = playerEntity[GraphicComponent.mapper]!!
        player.isReversed = false
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("bear"))
        }
    }
}
