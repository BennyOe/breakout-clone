package io.bennyoe.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.loaders.BitmapFontLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
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
) {
    BACKGROUND("bg2dark.jpg"),

}

enum class TextureAtlasAsset(
    val isSkinAtlas: Boolean,
    name: String,
    directory: String = "sprites",
    val descriptor: AssetDescriptor<TextureAtlas> = AssetDescriptor("$directory/$name", TextureAtlas::class.java)
) {
    BALLS(false, "balls.atlas"),
    BRIKCS(false, "bricks.atlas"),
    PLAYER(false, "player.atlas"),
    POWERUPS(false, "powerUps.atlas"),
    POWERUPTEXT(false, "powerUpText.atlas"),
    UI(true, "ui.atlas")
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
    EXPLOSION1("explosion1.mp3"),
    EXPLOSION2("explosion2.mp3"),
    FLINT3("flint3.mp3"),
    GAME_LOSE("game_lose.mp3"),
    GAME_WIN("game_win1.mp3"),
    STICKY1("sticky1.mp3"),
    STICKY2("sticky2.mp3"),
    STICKY_RELEASE("sticky-release.mp3"),
    BALL_LOST("ball-lost.mp3"),

   PU_CHANGE_SIZE("powerups/changeSize.mp3"),
   PU_EXPLOSION("powerups/explosion.mp3"),
   PU_HEART("powerups/heart.mp3"),
   PU_MULTIBALL("powerups/multiball.mp3"),
   PU_PENETRATION("powerups/penetration.mp3"),
   PU_REVERSE("powerups/reverse.mp3"),
   PU_SHOOTER("powerups/shooter.mp3"),
   PU_SPEED_UP("powerups/speedUp.mp3"),
   PU_STICKY("powerups/sticky.mp3"),
    PU_SHEEP("powerups/sheep.mp3"),
}

enum class MusicAsset(
    name: String,
    directory: String = "music",
    val descriptor: AssetDescriptor<Music> = AssetDescriptor("$directory/$name", Music::class.java)
) {
    BG_MUSIC("bgMusic.mp3"),
    SHOOTER_MUSIC("doom_short.mp3"),
    SHEEP_MUSIC("sheepMix.mp3"),
    REVERSE_MUSIC("reverse.mp3"),
}

enum class BitmapFontAsset(
    name: String,
    directory: String = "ui",
    val descriptor: AssetDescriptor<BitmapFont> = AssetDescriptor(
        "$directory/$name", BitmapFont::class.java,
        BitmapFontLoader.BitmapFontParameter().apply {
            atlasName = TextureAtlasAsset.UI.descriptor.fileName
        }),
) {
    BEAROUT("bearout.fnt"),
    TEST("myFont.fnt"),
    NORMAL("normalFont.fnt")

}
