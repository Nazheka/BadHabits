package epicarchitect.breakbadhabits.logic.habits.newarch.habits

import kotlin.time.Duration

interface HabitAbstinenceStatistics {
    fun averageDuration(): Duration?
    fun maxDuration(): Duration?
    fun minDuration(): Duration?
    fun durationSinceFirstTrack(): Duration?
}