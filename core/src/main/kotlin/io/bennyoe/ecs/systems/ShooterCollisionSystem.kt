package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Rectangle
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.BulletComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class ShooterCollisionSystem : IteratingSystem(
    allOf(BrickComponent::class, TransformComponent::class).get()
) {
    private lateinit var bullets: List<Entity>

    override fun update(deltaTime: Float) {
        bullets = engine.entities.filter { it[BulletComponent.mapper] != null }

        super.update(deltaTime)

        val entitiesToRemove = engine.entities.filter { entity ->
            val bullet = entity[BulletComponent.mapper]
            val transform = entity[TransformComponent.mapper]
            transform != null && bullet != null && transform.position.y > WORLD_HEIGHT
        }
        entitiesToRemove.forEach { engine.removeEntity(it) }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]!!
        val brick = entity[BrickComponent.mapper]!!
        bullets.forEach { bullet ->
            val bulletTransform = bullet[TransformComponent.mapper]!!
            if (bulletIntersectsBrick(bulletTransform, transform)) {
                brick.hitpoints = 0
                engine.removeEntity(bullet)
            }
        }
    }

    private fun bulletIntersectsBrick(bulletTransform: TransformComponent, transform: TransformComponent): Boolean {
        val bulletRect = Rectangle(bulletTransform.position.x, bulletTransform.position.y, bulletTransform.size.x, bulletTransform.size.y)
        val brickRect = Rectangle(transform.position.x, transform.position.y, transform.size.x, transform.size.y)
        return Intersector.overlaps(brickRect, bulletRect)
    }
}
