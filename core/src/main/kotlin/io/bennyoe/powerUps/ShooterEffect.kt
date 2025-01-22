package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.systems.ShooterSystem
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.get

class ShooterEffect : PowerUpEffect {
    private val playerShooterTexture = Texture("images/bear_shooter.png")
    private val playerTexture = Texture("images/bear-long-border.png")

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val graphics = playerEntity[GraphicComponent.mapper]!!

        graphics.sprite.run {
            setRegion(playerShooterTexture)
        }

        val shooterSystem = ShooterSystem()
        engine.addSystem(shooterSystem)
        ballEntity.addEffectTimer("SHOOTER", 5f) {
            engine.removeSystem(shooterSystem)
            graphics.sprite.run {
                setRegion(playerTexture)
            }
        }
    }
}
