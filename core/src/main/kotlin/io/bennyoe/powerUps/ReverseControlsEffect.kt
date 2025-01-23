package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PlayerComponent
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.get

class ReverseControlsEffect : PowerUpEffect {
    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val player = playerEntity[PlayerComponent.mapper]!!
        player.isReversed = true

        val graphics = playerEntity[GraphicComponent.mapper]!!

        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("reversed-controlls"))
        }

        playerEntity.addEffectTimer("REVERSE_CONTROLS", 5f) {
            player.isReversed = false
            graphics.sprite.run {
                setRegion(playerAtlas.findRegion("bear"))
            }
        }
    }
}
