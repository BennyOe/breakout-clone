package io.bennyoe.audio

import io.bennyoe.assets.MusicAsset
import io.bennyoe.assets.SoundAsset
import ktx.log.logger

const val MAX_SOUND_INSTANCES = 16
private val LOG = logger<AudioService>()

interface AudioService {
    fun play(soundAsset: SoundAsset, volume: Float = 1f) = Unit
    fun play(musicAsset: MusicAsset, volume: Float = 0.5f, loop: Boolean = true) = Unit
    fun pause()
    fun resume()
    fun stop(clearSounds: Boolean = true)
    fun update()
    fun isMusicTypePlaying(musicAsset: MusicAsset): Boolean
}
