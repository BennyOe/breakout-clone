package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.systems.ShapeRenderingSystem
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.get

class ExplodingEffect : PowerUpEffect {
    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
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
