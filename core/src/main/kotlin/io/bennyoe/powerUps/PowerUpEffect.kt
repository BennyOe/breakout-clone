package io.bennyoe.powerUps

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity

interface PowerUpEffect {
    fun apply(playerEntity: Entity, ballEntity: Entity, engine: Engine)
}
