package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with

class BrickSystem(
    private val viewport: Viewport
) : IteratingSystem(
    allOf(BrickComponent::class, TransformComponent::class, GraphicComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity has no TransformComponent" }

        val graphic = entity[GraphicComponent.mapper]
        requireNotNull(graphic) { "Entity has no GraphicComponent" }

        val brick = entity[BrickComponent.mapper]
        requireNotNull(brick) { "Entity has no BrickComponent" }

        if (brick.hitpoints <= 0) {
            engine.removeEntity(entity)
        }
    }

    fun initializeBricks(textureAtlas: TextureAtlas) {
        for (rowCount in 13..WORLD_HEIGHT.toInt()) {
            for (columnCount in 0 until WORLD_WIDTH.toInt() / 2) {
                if (Math.random() >= 0.5) {
                    engine.entity {
                        with<TransformComponent> {
                            position.set(columnCount.toFloat() * 2, rowCount.toFloat(), 0f)
                            size.set(2f, 1f)
                        }
                        with<BrickComponent>()
                        with<GraphicComponent> {
                            sprite.run {
                                setRegion(textureAtlas.findRegion("blue_brick"))
                                setOriginCenter()
                            }
                        }
                    }
                }
            }
        }
    }

}
