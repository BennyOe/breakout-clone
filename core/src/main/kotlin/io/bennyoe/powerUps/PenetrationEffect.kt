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

class PenetrationEffect(private val audioService: AudioService, private val assets: AssetStorage) : PowerUpEffect() {
    override val isAdditionalEffect = false
    override val powerUpType = PowerUpType.PENETRATION
    override var remainingTime = 5f
    private val ballAtlas by lazy { assets[TextureAtlasAsset.BALLS.descriptor] }

    override fun apply(playerEntity: Entity, engine: Engine) {
        audioService.play(SoundAsset.PU_PENETRATION)
        engine.entity {
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.PENETRATION
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }

        activateColorOverlay(Color(0f, 1f, 0f, 0.1f), engine)
        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            val graphicComponent = ball[GraphicComponent.mapper]!!
            graphicComponent.sprite.run {
                setRegion(ballAtlas.findRegion("Ball_Blue_Glass_trans-32x32"))
            }
            ballComponent.isPenetrating = true
        }
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) {
        val ballEntities = engine.entities.filter { it[BallComponent.mapper] != null }
        deactivateColorOverlay()
        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            val graphicComponent = ball[GraphicComponent.mapper]!!
            graphicComponent.sprite.run {
                setRegion(ballAtlas.findRegion("Ball_Yellow_Glossy_trans-32x32"))
            }
            ballComponent.isPenetrating = false
        }
    }
}
