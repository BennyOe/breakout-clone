package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import io.bennyoe.ecs.components.BulletComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with

class ShooterEffect : PowerUpEffect {
    private val playerTexture = Texture("images/bear_shooter.png")

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val graphics = playerEntity[GraphicComponent.mapper]!!
        val transform = playerEntity[TransformComponent.mapper]!!

        graphics.sprite.run {
            setRegion(playerTexture)
        }

engine.entity {
    with<TransformComponent>(){
        position.x = transform.position.x
        position.y = transform.position.y
        size.x = 1f
        size.y = 2f
    }
    with<BulletComponent>(){
        shapeRenderer.run {
            Rectangle(transform.position.x, transform.position.y, 1f, 2f)
        }
    }
}
    }
}
