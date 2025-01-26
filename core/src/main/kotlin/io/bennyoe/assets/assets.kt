package io.bennyoe.assets

import com.badlogic.gdx.assets.AssetDescriptor
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

