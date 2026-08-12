package com.sensipro.app.history

data class HistoryEntry(
    val id: String,
    val timestampMillis: Long,
    val deviceModel: String,
    val profile: String,
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniper: Int,
    val freeLook: Int
) {
    /** Encodes to a single pipe-delimited line for lightweight DataStore storage. */
    fun encode(): String = listOf(
        id, timestampMillis.toString(), deviceModel.replace("|", " "),
        profile, general, redDot, scope2x, scope4x, sniper, freeLook
    ).joinToString("|")

    companion object {
        fun decode(line: String): HistoryEntry? {
            val parts = line.split("|")
            if (parts.size != 10) return null
            return try {
                HistoryEntry(
                    id = parts[0],
                    timestampMillis = parts[1].toLong(),
                    deviceModel = parts[2],
                    profile = parts[3],
                    general = parts[4].toInt(),
                    redDot = parts[5].toInt(),
                    scope2x = parts[6].toInt(),
                    scope4x = parts[7].toInt(),
                    sniper = parts[8].toInt(),
                    freeLook = parts[9].toInt()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
