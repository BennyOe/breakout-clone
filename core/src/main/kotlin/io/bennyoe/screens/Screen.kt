package io.bennyoe.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.Main
import ktx.app.KtxScreen

abstract class Screen(
    private val game: Main,
    val viewport: Viewport = game.viewport,
    val batch: Batch = game.batch,
    val engine: Engine = game.engine
) : KtxScreen {

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
}
