package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<PowerUpTextSystem>()

class PowerUpTextSystem() : EntitySystem(), EntityListener {
    private val powerUpTextAtlas by lazy { TextureAtlas("sprites/powerUpText.atlas") }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(Family.all(PowerUpTextComponent::class.java).get(), this)
    }

    override fun entityAdded(entity: Entity) {
        // check if there is an text displayed and remove it before showing the new one
        val entities = engine.getEntitiesFor(Family.all(PowerUpTextComponent::class.java).get())
        entities?.filter { it != entity }?.forEach { engine.removeEntity(it) }

        val graphic = entity[GraphicComponent.mapper]!!
        val powerUp = entity[PowerUpTextComponent.mapper]!!
        val transform = entity[TransformComponent.mapper]!!
        val text = powerUpTextAtlas.findRegion(powerUp.powerUpType.type)
        graphic.setSpriteRegion(text)

        transform.setInitialPosition(
            WORLD_WIDTH / 2 - 5,
            WORLD_HEIGHT / 2,
            -2f
        )
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        engine.getEntitiesFor(Family.all(PowerUpTextComponent::class.java, TransformComponent::class.java).get())
            .forEach { entity ->
                val powerUp = entity[PowerUpTextComponent.mapper]!!
                val transform = entity[TransformComponent.mapper]!!

                powerUp.animationTime += deltaTime

                val progress = (powerUp.animationTime / powerUp.duration).coerceIn(0f, 1f)
                val interpolatedSize = Interpolation.fastSlow.apply(1f, powerUp.maxSize, progress)

                transform.size.set(interpolatedSize, interpolatedSize)
                val offsetX = (interpolatedSize - transform.size.x) / 2
                val offsetY = (interpolatedSize - transform.size.y) / 2

                transform.interpolatedPosition.set(
                    (WORLD_WIDTH / 2) - offsetX - 4,
                    (WORLD_HEIGHT / 2) - offsetY,
                    transform.position.z
                )

                if (progress >= 1f) {
                    engine.removeEntity(entity)
                }
            }
    }

    override fun entityRemoved(entity: Entity) {

    }
}
