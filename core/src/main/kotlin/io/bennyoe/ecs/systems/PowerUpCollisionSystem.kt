package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<PowerUpCollisionSystem>()

class PowerUpCollisionSystem(
    val playerEntity: Entity
) : IteratingSystem(
    allOf(PowerUpComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]!!
        val playerTransform = playerEntity[TransformComponent.mapper]!!
        val playerGraphics = playerEntity[GraphicComponent.mapper]!!
        val texture = Texture("images/bear-long-border-thug2.png")

        val powerUpRect = Rectangle(transform.position.x, transform.position.y, transform.size.x, transform.size.y)
        val playerRect = Rectangle(playerTransform.position.x, playerTransform.position.y, playerTransform.size.x, playerTransform.size.y)
        if (powerUpRect.overlaps(playerRect)) {
            playerGraphics.sprite.texture = texture
            engine.removeEntity(entity)
        }
    }
}
