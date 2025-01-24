package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.ecs.systems.ShapeRenderingSystem
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with

class StickyEffect : PowerUpEffect {
    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        engine.entity {
            with<PowerUpTextComponent>{
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

        val colorOverlaySystem = ShapeRenderingSystem(Color(1f, 1f, 0f, 0.1f))
        engine.addSystem(colorOverlaySystem)
        playerEntity.addEffectTimer("STICKY", 5f) {
            player.isSticky = false
            engine.removeSystem(colorOverlaySystem)
            graphics.sprite.run {
                setRegion(playerAtlas.findRegion("bear"))
            }
        }
    }
}
