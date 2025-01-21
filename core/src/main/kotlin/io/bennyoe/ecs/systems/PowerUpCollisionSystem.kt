package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Rectangle
import io.bennyoe.ecs.components.PowerUpComponent
import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.components.TransformComponent
import io.bennyoe.powerUps.BallSpeedUpEffect
import io.bennyoe.powerUps.ChangeSizeEffect
import io.bennyoe.powerUps.ExplodingEffect
import io.bennyoe.powerUps.MultiballEffect
import io.bennyoe.powerUps.PenetrationEffect
import io.bennyoe.powerUps.PowerUpEffect
import io.bennyoe.powerUps.ShooterEffect
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<PowerUpCollisionSystem>()

class PowerUpCollisionSystem(
    val playerEntity: Entity,
    val ballEntity: Entity,
) : IteratingSystem(
    allOf(PowerUpComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]!!
        val powerUp = entity[PowerUpComponent.mapper]!!
        val playerTransform = playerEntity[TransformComponent.mapper]!!

        val powerUpRect = Rectangle(transform.position.x, transform.position.y, transform.size.x, transform.size.y)
        val playerRect = Rectangle(playerTransform.position.x, playerTransform.position.y, playerTransform.size.x, playerTransform.size.y)

        if (powerUpRect.overlaps(playerRect)) {
            val effect = getPowerUpEffect(powerUp.powerUpType)
            effect.apply(playerEntity, ballEntity, engine)
            engine.removeEntity(entity)
        }
        if (transform.position.y < 0 - transform.size.y){
            engine.removeEntity(entity)
        }
    }

   private fun getPowerUpEffect(type: PowerUpType): PowerUpEffect {
        return when (type) {
            PowerUpType.SHOOTER -> ShooterEffect()
            PowerUpType.PENETRATION -> PenetrationEffect()
            PowerUpType.FAST_BALL -> BallSpeedUpEffect()
            PowerUpType.MULTIBALL -> MultiballEffect()
//            PowerUpType.BONUS_HEART -> BonusHeartPowerUp()
            PowerUpType.CHANGE_SIZE -> ChangeSizeEffect()
//            PowerUpType.STICKY_BALL -> StickyBallPowerUp()
            PowerUpType.EXPLODING_BALL -> ExplodingEffect()
//            PowerUpType.REVERSE_CONTROL -> ReverseControlPowerUp()
            else -> ChangeSizeEffect()
        }
    }
}
