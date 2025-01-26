package io.bennyoe.screens

import io.bennyoe.Main
import io.bennyoe.assets.AnimationAsset
import io.bennyoe.assets.MusicAsset
import io.bennyoe.assets.SoundAsset
import io.bennyoe.assets.TextureAsset
import io.bennyoe.assets.TextureAtlasAsset
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.logger

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: Main, val assets: AssetStorage = game.assets) : Screen(game) {

    override fun show() {
        super.show()
        val oldTime = System.currentTimeMillis()
        val assetRefs = gdxArrayOf(
            AnimationAsset.entries.map { assets.loadAsync(it.descriptor) },
            TextureAsset.entries.map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.entries.map { assets.loadAsync(it.descriptor) },
            SoundAsset.entries.map { assets.loadAsync(it.descriptor) },
            MusicAsset.entries.map { assets.loadAsync(it.descriptor) },
        ).flatten()
        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.debug { "It took ${System.currentTimeMillis() - oldTime}ms to load the assets" }
            assetsLoaded()
        }
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game, assets))
        game.setScreen<GameScreen>()
    }
}
