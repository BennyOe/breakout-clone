package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import io.bennyoe.assets.TextureAtlasAsset
import io.bennyoe.ecs.components.BrickComponent
import io.bennyoe.ecs.components.ExplodingComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.getSystem
import ktx.ashley.with
import ktx.assets.async.AssetStorage

class BrickSystem(
    private val assets: AssetStorage,
    ) : IteratingSystem(
    allOf(BrickComponent::class, TransformComponent::class, GraphicComponent::class).get()
) {
    private val brickAtlas by lazy { assets[TextureAtlasAsset.BRIKCS.descriptor] }
    private val gameStateSystem by lazy { engine.getSystem<GameStateSystem>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity has no TransformComponent" }

        val graphic = entity[GraphicComponent.mapper]
        requireNotNull(graphic) { "Entity has no GraphicComponent" }

        val brick = entity[BrickComponent.mapper]
        requireNotNull(brick) { "Entity has no BrickComponent" }

        if (brick.hitPoints <= 0) {
            gameStateSystem.addScore(5 * gameStateSystem.scoreMultiplier)
            engine.removeEntity(entity)
        }

        if (brick.hitPoints > 0 && brick.type.destructible) {
            graphic.sprite.run {
                setRegion(brickAtlas.findRegion(brick.type.atlasKey, brick.type.hitPoints + 1 - brick.hitPoints))
            }
        }
    }

    fun initializeBricks(textureAtlas: TextureAtlas) {
        for (rowCount in 13..<WORLD_HEIGHT.toInt()) {
            for (columnCount in 0 until WORLD_WIDTH.toInt() / 2) {
                if (Math.random() >= 0.5) {
                    engine.entity {
                        with<TransformComponent> {
                            setInitialPosition(columnCount.toFloat() * 2, rowCount.toFloat(), 0f)
                            size.set(2f, 1f)
                        }
                        val brickComponent = with<BrickComponent>() {
                            hasPowerUp = if (Math.random() >= 0.8) true else false
                            powerUpType = PowerUpType.entries.toTypedArray().random()
                            type = BrickType.entries.filter { it.destructible }.random()
                            hitPoints = type.hitPoints
                        }
                        with<GraphicComponent> {
                            sprite.run {
                                setRegion(textureAtlas.findRegion(brickComponent.type.atlasKey))
                                setOriginCenter()
                            }
                        }
                        with<ExplodingComponent>()
                    }
                }
            }
        }
    }

    // Maybe use later
    private fun getBrickIndex(type: BrickType, brick: BrickComponent): Int {
        return when (type) {
            BrickType.BLUE -> type.hitPoints + 1 - brick.hitPoints
            BrickType.YELLOW -> type.hitPoints + 1 - brick.hitPoints
            BrickType.GREEN -> type.hitPoints + 1 - brick.hitPoints
            BrickType.ORANGE -> type.hitPoints + 1 - brick.hitPoints
            BrickType.RED -> type.hitPoints + 1 - brick.hitPoints
            BrickType.PURPLE -> type.hitPoints + 1 - brick.hitPoints
            BrickType.STONE -> type.hitPoints + 1 - brick.hitPoints
            BrickType.NONE -> type.hitPoints + 1 - brick.hitPoints
        }
    }

}
