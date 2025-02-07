package io.bennyoe.ecs.systems

enum class BrickType(
    val atlasKey: String,
    val hitPoints: Int,
    val destructible: Boolean = true
) {
    BLUE("blue_brick", 1),
    YELLOW("yellow_brick", 2),
    GREEN("green_brick", 3),
    ORANGE("orange_brick", 4),
    RED("red_brick", 5),
    PURPLE("purple_brick", 6),
//    STONE("stone_brick", 1, false),
//    WOOD("wood_brick", 1, false)
}
