package io.bennyoe.audio

import com.badlogic.gdx.utils.Pool
import io.bennyoe.assets.SoundAsset

class SoundRequest : Pool.Poolable {
    lateinit var soundAsset: SoundAsset
    var volume = 0.5f

    override fun reset() {
        volume = 0.5f
    }
}
