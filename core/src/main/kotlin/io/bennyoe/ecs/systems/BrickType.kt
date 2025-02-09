package io.bennyoe.ecs.systems

enum class BrickType(
    val atlasKey: String,
    val hitPoints: Int,
    val destructible: Boolean = true
) {
    BLUE("blue_brick", 1),
    YELLOW("yellow_brick", 2),
    GREEN("green_brick", 4),
    ORANGE("orange_brick", 6),
    RED("red_brick", 8),
    PURPLE("purple_brick", 10),
    STONE("stone_brick", 1, false),
    NONE("empty_brick", 1, false)
//    WOOD("wood_brick", 1, false)
}
