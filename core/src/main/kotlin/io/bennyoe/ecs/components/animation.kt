package io.bennyoe.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

private const val DEFAULT_FRAME_DURATION = 1 / 20f

enum class AnimationType(
    val atlasKey: String,
    val playMode: PlayMode = PlayMode.LOOP,
    val speedRate: Float = 1f
) {
    NONE(""),
    EXPLOSION("1", PlayMode.NORMAL, 0.8f)
}

class Animation2D(
    val type: AnimationType,
    keyFrames: Array<out TextureRegion>,
    playMode: PlayMode = PlayMode.LOOP,
    speedRate: Float = 1f
) : Animation<TextureRegion>((DEFAULT_FRAME_DURATION) / speedRate, keyFrames, playMode)

class AnimationComponent : Component, Pool.Poolable {
    var type = AnimationType.NONE
    var stateTime = 1f
    lateinit var animation: Animation2D

    override fun reset() {
        type = AnimationType.NONE
    }

    companion object {
        val mapper = mapperFor<AnimationComponent>()
    }
}
