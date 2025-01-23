package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.UNIT_SCALE
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.ecs.systems.ShapeRenderingSystem
import io.bennyoe.ecs.systems.addEffectTimer
import ktx.ashley.get
import kotlin.math.cos
import kotlin.math.sin

class ChangeSizeEffect : PowerUpEffect {
    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }

    override fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine) {
        val playerTransform = playerEntity[TransformComponent.mapper]!!
        playerTransform.size.x = minOf(playerTransform.size.x + 4, 10f)

        val graphics = playerEntity[GraphicComponent.mapper]!!
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("change-size"))
        }

        val colorOverlaySystem = ShapeRenderingSystem(Color(1f, 0.75f, 0.8f, 0.1f))
        engine.addSystem(colorOverlaySystem)

        playerEntity.addEffectTimer("ChangeSize", 5f) {
            val stickyBalls = engine.entities.filter { it[BallComponent.mapper]?.isSticky == true }
            stickyBalls.forEach {
                val ball = it[BallComponent.mapper]!!
                ball.isSticky = false

                val angle = map(ball.offsetXToPlayer, 0f, playerTransform.size.x, Math.toRadians(140.0).toFloat(), Math.toRadians(40.0).toFloat())
                ball.xSpeed = 6 * cos(angle) * 1.8f
                ball.ySpeed = 6 * sin(angle) * 1.8f
                ball.offsetXToPlayer = 0f
            }
            engine.removeSystem(colorOverlaySystem)
            graphics.sprite.run {
                setRegion(playerAtlas.findRegion("bear"))
            }
            playerTransform.size.x = 128 * UNIT_SCALE
        }
    }

    private fun map(value: Float, orgStart: Float, orgStop: Float, targetStart: Float, targetStop: Float): Float {
        return targetStart + (value - orgStart) * (targetStop - targetStart) / (orgStop - orgStart)
    }
}
