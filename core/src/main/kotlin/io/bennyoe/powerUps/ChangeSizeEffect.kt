package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import io.bennyoe.UNIT_SCALE
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.PowerUpTextComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.utillity.Mapper.Companion.mapToRange
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import kotlin.math.cos
import kotlin.math.sin

class ChangeSizeEffect(private val audioService: AudioService) : PowerUpEffect() {
    override val isAdditionalEffect = false
    override val powerUpType = PowerUpType.CHANGE_SIZE
    override var remainingTime = 5f

    private val playerAtlas by lazy { TextureAtlas("sprites/player.atlas") }

    override fun apply(playerEntity: Entity, engine: Engine) {
        audioService.play(SoundAsset.PU_CHANGE_SIZE)
        engine.entity {
            with<PowerUpTextComponent> {
                powerUpType = PowerUpType.CHANGE_SIZE
            }
            with<TransformComponent>()
            with<GraphicComponent>()
        }

        val playerTransform = playerEntity[TransformComponent.mapper]!!
        playerTransform.size.x = minOf(playerTransform.size.x + 4, 10f)

        val graphics = playerEntity[GraphicComponent.mapper]!!
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("change-size"))
        }
        activateColorOverlay(Color(1f, 0.75f, 0.8f, 0.1f), engine)
    }

    override fun deactivate(playerEntity: Entity, engine: Engine) {
        val graphics = playerEntity[GraphicComponent.mapper]!!
        val playerTransform = playerEntity[TransformComponent.mapper]!!
        playerTransform.size.x = minOf(playerTransform.size.x + 4, 10f)
        val stickyBalls = engine.entities.filter { it[BallComponent.mapper]?.isSticky == true }
        stickyBalls.forEach {
            val ball = it[BallComponent.mapper]!!
            ball.isSticky = false

            val angle =
                mapToRange(ball.offsetXToPlayer, 0f, playerTransform.size.x, Math.toRadians(140.0).toFloat(), Math.toRadians(40.0).toFloat())
            ball.xSpeed = 6 * cos(angle) * 1.8f
            ball.ySpeed = 6 * sin(angle) * 1.8f
            ball.offsetXToPlayer = 0f
        }
        deactivateColorOverlay()
        graphics.sprite.run {
            setRegion(playerAtlas.findRegion("bear"))
        }
        playerTransform.size.x = 128 * UNIT_SCALE
    }
}
