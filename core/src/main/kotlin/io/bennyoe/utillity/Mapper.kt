package io.bennyoe.utillity

class Mapper {
    companion object {
        fun mapToRange(value: Float, orgStart: Float, orgStop: Float, targetStart: Float, targetStop: Float): Float {
            return targetStart + (value - orgStart) * (targetStop - targetStart) / (orgStop - orgStart)
        }
    }
}
