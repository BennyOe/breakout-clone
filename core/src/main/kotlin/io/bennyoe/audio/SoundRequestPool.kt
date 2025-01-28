package io.bennyoe.audio

import com.badlogic.gdx.utils.Pool

class SoundRequestPool : Pool<SoundRequest>() {
    override fun newObject(): SoundRequest = SoundRequest()
}
