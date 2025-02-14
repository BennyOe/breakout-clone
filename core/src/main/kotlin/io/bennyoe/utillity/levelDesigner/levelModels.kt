import io.bennyoe.ecs.components.PowerUpType
import io.bennyoe.ecs.systems.BrickType

data class BearoutMap(
    var name: String = "",
    var author: String = "",
    var difficulty: Int = 1,
    val rows: Int = 0,
    val columns: Int = 0,
    val grid: Array<Array<MapEntry?>> = Array(rows) { Array(columns) { null } }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BearoutMap

        if (rows != other.rows) return false
        if (columns != other.columns) return false
        if (name != other.name) return false
        if (!grid.contentDeepEquals(other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + columns
        result = 31 * result + name.hashCode()
        result = 31 * result + grid.contentDeepHashCode()
        return result
    }

    override fun toString(): String {
        return "$name von $author Schwierigkeit: $difficulty"
    }
}
data class MapEntry(
    var type: BrickType? = null,
    var powerUp: PowerUpType? = null
)

data class SelectedBrick(
    val column: Float,
    val brickType: BrickType
)

data class SelectedPowerUp(
    val column: Float,
    val powerUpType: PowerUpType
)
