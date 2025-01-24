package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import io.bennyoe.UNIT_SCALE
import ktx.ashley.mapperFor

class GraphicComponent : Component, Pool.Poolable {
    val sprite = Sprite()

    override fun reset() {
        sprite.texture = null
        sprite.setColor(1f, 1f, 1f, 1f)
        sprite.setOriginCenter()
    }

    fun setSpriteRegion(frame: TextureRegion?) {
        sprite.run {
            setRegion(frame)
            if (frame != null) {
                setSize(frame.regionWidth * UNIT_SCALE, frame.regionHeight * UNIT_SCALE)
                setOriginCenter()
            }
        }
    }

    companion object {
        val mapper = mapperFor<GraphicComponent>()
    }
}
