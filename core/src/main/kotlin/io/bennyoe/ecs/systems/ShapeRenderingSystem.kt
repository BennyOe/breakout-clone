//package io.bennyoe.ecs.systems
//
//import com.badlogic.ashley.core.Entity
//import com.badlogic.ashley.systems.IteratingSystem
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer
//import com.badlogic.gdx.utils.viewport.Viewport
//import io.bennyoe.ecs.components.PowerUpComponent
//import io.bennyoe.ecs.components.TransformComponent
//import ktx.ashley.allOf
//import ktx.ashley.get
//import ktx.graphics.use
//
//class ShapeRenderingSystem(val viewport: Viewport, val shapeRenderer: ShapeRenderer) : IteratingSystem(
//    allOf(PowerUpComponent::class).get()
//) {
//    override fun update(deltaTime: Float) {
//        viewport.apply()
//        shapeRenderer.use(ShapeRenderer.ShapeType.Filled) {
//            super.update(deltaTime)
//        }
//    }
//
//    override fun processEntity(entity: Entity, deltaTime: Float) {
//        val powerUpTransform = entity[TransformComponent.mapper]!!
//        powerUpTransform.position.y -= 1f
//        powerUpTransform.let {
//            shapeRenderer.circle(it.position.x, it.position.y, 10f)
//
//        }
//    }
//}
