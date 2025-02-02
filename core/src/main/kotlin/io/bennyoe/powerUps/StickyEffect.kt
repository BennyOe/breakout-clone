package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
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

class StickyEffect(private val audioService: AudioService) : PowerUpEffect() {
    override val isAdditionalEffect = false
    override val powerUpType = PowerUpType.STICKY_BALL
    override var remainingTime = 5f

    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }

    override fun apply(playerEntity: Entity, engine: Engine) {
        audioService.play(SoundAsset.PU_STICKY)
        engine.entity {
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.STICKY_BALL
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        val player = playerEntity[PlayerComponent.mapper]!!
        player.isSticky = true

        val graphics = playerEntity[GraphicComponent.mapper]!!
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("bear-sticky"))
        }
        activateColorOverlay(Color(1f, 1f, 0f, 0.1f), engine)
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) {
        val graphics = playerEntity[GraphicComponent.mapper]!!
        val player = playerEntity[PlayerComponent.mapper]!!
        player.isSticky = false
        deactivateColorOverlay()
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("bear"))
        }
    }
}
