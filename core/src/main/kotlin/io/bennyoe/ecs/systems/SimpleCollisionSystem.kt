package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.ecs.components.BallComponent
import io.bennyoe.ecs.components.GraphicComponent
import io.bennyoe.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class SimpleCollisionSystem(val viewport: Viewport) : IteratingSystem(
    allOf(BallComponent::class, TransformComponent::class, GraphicComponent::class).get()
){
    private val tmpVec = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val ball = entity[BallComponent.mapper]
        require(ball != null) { "entity has no ball entity" }

        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "entity has no transform entity" }

        val graphics = entity[GraphicComponent.mapper]
        require(graphics != null) { "entity has no graphics entity" }

        // left collision
        if (transform.position.x <= transform.size.x) {
            transform.position.x = transform.size.x
            reverseX(ball)
        }
        // right collision
        if (transform.position.x >= viewport.worldWidth - transform.size.x) {
            transform.position.x = viewport.worldWidth - transform.size.x
            reverseX(ball)
        }
        // top collision
        if (transform.position.y >= viewport.worldHeight - transform.size.y) {
            transform.position.y = viewport.worldHeight - transform.size.y
            reverseY(ball)
        }
        // bottom collision
        if (transform.position.y <= 0 + transform.size.y) {
            transform.position.y = transform.size.y
            reverseY(ball)
        }


    }

    private fun reverseX(ball: BallComponent){
        ball.xSpeed *= -1
    }

    private fun reverseY(ball: BallComponent){
        ball.ySpeed *= -1
    }
}
