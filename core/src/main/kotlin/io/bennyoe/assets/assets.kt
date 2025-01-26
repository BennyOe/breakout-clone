package io.bennyoe.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

enum class AnimationAsset(
    name: String,
    directory: String = "animation",
    val descriptor: AssetDescriptor<TextureAtlas> = AssetDescriptor("$directory/$name", TextureAtlas::class.java)
) {
    EXPLOSION("explosion.atlas")
}

enum class TextureAsset(
    name: String,
    directory: String = "images",
    val descriptor: AssetDescriptor<Texture> = AssetDescriptor("$directory/$name", Texture::class.java)
){
    BACKGROUND("bg2dark.jpg")
}

enum class TextureAtlasAsset(
    name: String,
    directory: String = "sprites",
    val descriptor: AssetDescriptor<TextureAtlas> = AssetDescriptor("$directory/$name", TextureAtlas::class.java)
){
    BALLS("balls.atlas"),
    BRIKCS("bricks.atlas"),
    PLAYER("player.atlas"),
    POWERUPS("powerUps.atlas"),
    POWERUPTEXT("powerUpText.atlas")
}

enum class SoundAsset(
    name: String,
    directory: String = "sfx",
    val descriptor: AssetDescriptor<Sound> = AssetDescriptor("$directory/$name", Sound::class.java)
) {
    BEAR_HIT("bear-hit.mp3"),
    BRICK_HIT("brick-hit.mp3"),
    POWER_UP_FALLING("power-up-falling.mp3"),
    WALL_HIT("wall-hit.mp3"),
}

enum class MusicAsset(
    name: String,
    directory: String = "music",
    val descriptor: AssetDescriptor<Music> = AssetDescriptor("$directory/$name", Music::class.java)
){
    BG_MUSIC("bgMusic.mp3"),
}

