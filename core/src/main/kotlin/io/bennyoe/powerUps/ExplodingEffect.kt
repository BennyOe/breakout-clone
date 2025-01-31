package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import io.bennyoe.assets.TextureAtlasAsset
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.ecs.systems.ShapeRenderingSystem
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.assets.async.AssetStorage

class ExplodingEffect(assets: AssetStorage) : PowerUpEffect {
    private val playerAtlas by lazy {assets[TextureAtlasAsset.PLAYER.descriptor]}

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        engine.entity {
            with<PowerUpTextComponent>{
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

        ballEntities.forEach { ball ->
            val ballComponent = ball[BallComponent.mapper]!!
            ballComponent.isExploding = true
            val colorOverlaySystem = ShapeRenderingSystem(Color(0f, 0f, 1f, 0.1f))
            engine.addSystem(colorOverlaySystem)
            ball.addEffectTimer("Exploding", 5f) {
                ballComponent.isExploding = false
                engine.removeSystem(colorOverlaySystem)
                graphics.sprite.run {
                    setRegion(playerAtlas.findRegion("bear"))
                }
            }
        }
    }
}
