package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem

class SystemManager(private val engine: Engine) {
    private val systems = mutableListOf<EntitySystem>()

    fun addSystem(system: EntitySystem): EntitySystem{
        systems.add(system)
        engine.addSystem(system)
        return engine.getSystem(system::class.java)
    }

    fun removeSystem(system: EntitySystem){
        systems.remove(system)
        engine.removeSystem(system)
    }

    fun removeAllSystems(){
        systems.forEach { engine.removeSystem(it) }
        systems.clear()
    }
}
