package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.bennyoe.UNIT_SCALE
import io.bennyoe.WORLD_HEIGHT
import io.bennyoe.WORLD_WIDTH
import ktx.graphics.use

class ShapeRenderingSystem(val color: Color) : EntitySystem() {
    private val shapeRenderer = ShapeRenderer()

    override fun update(deltaTime: Float) {
        shapeRenderer.use(ShapeRenderer.ShapeType.Filled) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            it.color = color
            it.rect(0f, 0f, WORLD_WIDTH / UNIT_SCALE, WORLD_HEIGHT / UNIT_SCALE)
        }
        super.update(deltaTime)
    }
}
