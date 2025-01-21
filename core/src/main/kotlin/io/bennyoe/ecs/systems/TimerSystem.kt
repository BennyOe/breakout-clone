package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.bennyoe.ecs.components.TimerComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.remove

class TimerSystem : IteratingSystem(
    allOf(TimerComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val timer = entity[TimerComponent.mapper]!!

        val expiredTimer = mutableListOf<String>()
        timer.timers.forEach { (effect, timerData) ->
            timerData.duration -= deltaTime
            if (timerData.duration <= 0f) {
                timerData.onExpire.invoke()
                expiredTimer.add(effect)
            }
        }

        expiredTimer.forEach { effect ->
            timer.timers.remove(effect)
        }

        if (timer.timers.isEmpty()) {
            entity.remove<TimerComponent>()
        }
    }
}

fun Entity.addEffectTimer(effect: String, duration: Float, onExpire: () -> Unit) {
    val timer = this.get<TimerComponent>() ?: TimerComponent().also { this.add(it) }
    timer.timers[effect] = TimerComponent.TimerData(duration, onExpire)
}
