package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Rectangle
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.PowerUpComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.powerUps.BallSpeedUpEffect
import io.bennyoe.powerUps.ChangeSizeEffect
import io.bennyoe.powerUps.ExplodingEffect
import io.bennyoe.powerUps.ExtraLiveEffect
import io.bennyoe.powerUps.MultiballEffect
import io.bennyoe.powerUps.PenetrationEffect
import io.bennyoe.powerUps.PowerUpEffect
import io.bennyoe.powerUps.ReverseControlsEffect
import io.bennyoe.powerUps.ShooterEffect
import io.bennyoe.powerUps.StickyEffect
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.getSystem
import ktx.assets.async.AssetStorage
import ktx.log.logger

private val LOG = logger<PowerUpCollisionSystem>()

class PowerUpCollisionSystem(
    val playerEntity: Entity,
    val assets: AssetStorage,
    val audioService: AudioService
) : IteratingSystem(
    allOf(PowerUpComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val gameStateSystem = engine.getSystem<GameStateSystem>()
        val transform = entity[TransformComponent.mapper]!!
        val powerUp = entity[PowerUpComponent.mapper]!!
        val playerTransform = playerEntity[TransformComponent.mapper]!!

        val powerUpRect = Rectangle(transform.position.x, transform.position.y, transform.size.x, transform.size.y)
        val playerRect = Rectangle(playerTransform.position.x, playerTransform.position.y, playerTransform.size.x, playerTransform.size.y)

        // powerUp collected
        if (powerUpRect.overlaps(playerRect)) {
            val effect = getPowerUpEffect(powerUp.powerUpType)
            gameStateSystem.activatePowerUp(effect)
            engine.removeEntity(entity)
        }
        if (transform.position.y < 0 - transform.size.y) {
            engine.removeEntity(entity)
        }
    }

    private fun getPowerUpEffect(type: PowerUpType): PowerUpEffect {
        return when (type) {
            PowerUpType.SHOOTER -> ShooterEffect(audioService)
            PowerUpType.PENETRATION -> PenetrationEffect(audioService)
            PowerUpType.FAST_BALL -> BallSpeedUpEffect(audioService)
            PowerUpType.MULTIBALL -> MultiballEffect(audioService)
            PowerUpType.BONUS_HEART -> ExtraLiveEffect(audioService)
            PowerUpType.CHANGE_SIZE -> ChangeSizeEffect(audioService)
            PowerUpType.STICKY_BALL -> StickyEffect(audioService)
            PowerUpType.EXPLODING_BALL -> ExplodingEffect(assets, audioService)
            PowerUpType.REVERSE_CONTROL -> ReverseControlsEffect(audioService)
        }
    }
}
