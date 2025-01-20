package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import io.bennyoe.ecs.components.TimerComponent
import ktx.ashley.allOf
import ktx.ashley.get

class TimerSystem : IteratingSystem(
    allOf(TimerComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val timer = entity[TimerComponent.mapper]!!
        timer.timer -= deltaTime

        if (timer.timer < 0f) {
            timer.onExpire.invoke()
            entity.remove(TimerComponent::class.java)
        }
    }
}
