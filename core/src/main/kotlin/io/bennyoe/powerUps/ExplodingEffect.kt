package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import io.bennyoe.assets.SoundAsset
import io.bennyoe.assets.TextureAtlasAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.assets.async.AssetStorage

class ExplodingEffect(assets: AssetStorage, private val audioService: AudioService) : PowerUpEffect() {
    override val isAdditionalEffect = false
    override val powerUpType = PowerUpType.EXPLODING_BALL
    override var remainingTime = 5f

    private val playerAtlas by lazy { assets[TextureAtlasAsset.PLAYER.descriptor] }

    override fun apply(playerEntity: Entity, engine: Engine) {
        audioService.play(SoundAsset.PU_EXPLOSION)
        engine.entity {
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.EXPLODING_BALL
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }

        val graphics = playerEntity[GraphicComponent.mapper]!!
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("explosion"))
        }
        activateColorOverlay(Color(0f, 0f, 1f, 0.1f), engine)
        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.isExploding = true
        }
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) {
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }
        val graphics = playerEntity[GraphicComponent.mapper]!!
        deactivateColorOverlay()
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("bear"))
        }

        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.isExploding = false
        }
    }
}
