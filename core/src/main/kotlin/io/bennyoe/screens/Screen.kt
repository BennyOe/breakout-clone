package io.bennyoe.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import io.bennyoe.Main
import io.bennyoe.audio.AudioService
import ktx.app.KtxScreen

abstract class Screen(
    val game: Main,
    val viewport: Viewport = game.viewport,
    val batch: Batch = game.batch,
    val engine: Engine = game.engine,
    val audioService: AudioService = game.audioService,
    val stage: Stage = game.stage
) : KtxScreen {

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
}
