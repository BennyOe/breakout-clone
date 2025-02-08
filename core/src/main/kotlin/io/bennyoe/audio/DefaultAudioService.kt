package io.bennyoe.audio

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import io.bennyoe.assets.MusicAsset
import io.bennyoe.assets.SoundAsset
import ktx.assets.async.AssetStorage
import ktx.log.logger
import java.util.*
import kotlin.math.max

private val LOG = logger<DefaultAudioService>()

class DefaultAudioService(val assets: AssetStorage) : AudioService {
    private val soundCache = EnumMap<SoundAsset, Sound>(SoundAsset::class.java)
    private val soundRequestPool = SoundRequestPool()
    private val soundRequests = EnumMap<SoundAsset, SoundRequest>(SoundAsset::class.java)
    private var currentMusic: Music? = null
    private var currentMusicAsset = MusicAsset.BG_MUSIC

    override fun play(soundAsset: SoundAsset, volume: Float) {
        when {
            soundAsset in soundRequests -> {
                LOG.debug { "Sound is played twice" }
                soundRequests[soundAsset]?.let { request ->
                    request.volume = max(request.volume, volume)
                }
            }

            soundRequests.size > MAX_SOUND_INSTANCES -> {
                LOG.debug { "Max number of soundrequests reached" }
                return
            }

            else -> {
                // new request
                if (soundAsset.descriptor !in assets) {
                    // sound not loaded -> error
                    LOG.error { "Sound $soundAsset is not loaded" }
                    return
                } else if (soundAsset !in soundCache) {
                    // cache sound for faster access in the future
                    LOG.debug { "Adding sound $soundAsset to sound cache" }
                    soundCache[soundAsset] = assets[soundAsset.descriptor]
                }

                // get request instance from pool and add it to the queue
                LOG.debug { "New sound request for sound $soundAsset. Free request objects: ${soundRequestPool.free}" }
                soundRequests[soundAsset] = soundRequestPool.obtain().apply {
                    this.soundAsset = soundAsset
                    this.volume = volume
                }
            }
        }
    }

    override fun play(musicAsset: MusicAsset, volume: Float, loop: Boolean) {
        if (currentMusic != null) {
            currentMusic?.stop()
        }

        if (musicAsset.descriptor !in assets) {
            LOG.error { "Music $musicAsset is not loaded" }
            return
        }

        currentMusic = assets[musicAsset.descriptor]

        currentMusic?.apply {
            this.volume = volume
            this.isLooping = loop
            play()
        }
        currentMusicAsset = musicAsset
    }

    override fun pause() {
        currentMusic?.pause()
    }

    override fun resume() {
        currentMusic?.play()
    }

    override fun stop(clearSounds: Boolean) {
        currentMusic?.stop()
        if (clearSounds) soundRequests.clear()
    }

    override fun update() {
        if (!soundRequests.isEmpty()) {
            soundRequests.values.forEach { request ->
                soundCache[request.soundAsset]?.play(request.volume)
                soundRequestPool.free(request)
            }
            soundRequests.clear()
        }
    }

    override fun isMusicTypePlaying(musicAsset: MusicAsset): Boolean {
        return currentMusicAsset == musicAsset
    }
}
