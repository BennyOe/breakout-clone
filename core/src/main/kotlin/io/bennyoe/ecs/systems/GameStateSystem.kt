package io.bennyoe.ecs.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import io.bennyoe.assets.SoundAsset
import io.bennyoe.audio.AudioService
import io.bennyoe.ecs.components.PlayerComponent
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<GameStateSystem>()

class GameStateSystem(private val audioService: AudioService) : EntitySystem() {

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val player = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).first()
        val playerComponent = player[PlayerComponent.mapper]!!
        if (playerComponent.lives <= 0) {
            LOG.debug { "GAME OVER" }
            audioService.play(SoundAsset.GAME_LOSE)
            // TODO next line must be removed and the game over screen should be called
            playerComponent.lives = 4
        }
    }
}
