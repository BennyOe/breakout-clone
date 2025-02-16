package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.GdxRuntimeException
import io.bennyoe.ecs.components.Animation2D
import io.bennyoe.ecs.components.AnimationComponent
import io.bennyoe.ecs.components.AnimationType
import io.bennyoe.ecs.components.GraphicComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger
import java.util.EnumMap

private val LOG = logger<AnimationSystem>()

class AnimationSystem(
    private val atlas: TextureAtlas
) : IteratingSystem(allOf(AnimationComponent::class, GraphicComponent::class).get()), EntityListener {
    private val animationCache = EnumMap<AnimationType, Animation2D>(AnimationType::class.java)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        entity[AnimationComponent.mapper]?.let { aniCmp ->
            aniCmp.animation = getAnimation(aniCmp.type)
            val frame = aniCmp.animation.getKeyFrame(aniCmp.stateTime)
            entity[GraphicComponent.mapper]?.setSpriteRegion(frame)
        }
    }

    private fun getAnimation(type: AnimationType): Animation2D {
        var animation = animationCache[type]
        if (animation == null) {
            val regions = atlas.findRegions(type.atlasKey)
            if (regions.isEmpty) {
                throw GdxRuntimeException("animation $animation not found")
                // could render error animation instead of throwing error
            } else {
                LOG.debug { "Adding animation of type $type with ${regions.size} regions" }
            }
            animation = Animation2D(type, regions, type.playMode, type.speedRate)
            animationCache[type] = animation
        }
        return animation
    }

    override fun entityRemoved(entity: Entity) = Unit

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val aniCmp = entity[AnimationComponent.mapper]!!
        val graphic = entity[GraphicComponent.mapper]!!

        if (aniCmp.type == AnimationType.NONE) {
            LOG.error { "No type specified for animation component $aniCmp for entity $entity" }
            return
        }
        if (aniCmp.type == aniCmp.animation.type) {
            aniCmp.stateTime += deltaTime
        } else {
            aniCmp.stateTime = 0f
            aniCmp.animation = getAnimation(aniCmp.type)
        }
        val frame = aniCmp.animation.getKeyFrame(aniCmp.stateTime)
        graphic.setSpriteRegion(frame)
    }
}
