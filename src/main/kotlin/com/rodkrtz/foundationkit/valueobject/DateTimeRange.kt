package com.rodkrtz.foundationkit.valueobject

import java.time.Duration
import java.time.Instant

/**
 * Value object representing a time interval.
 *
 * Useful for representing periods, time windows, operating hours, etc.
 * Provides methods for duration calculation, containment checks, and overlap detection.
 *
 * @property start The start instant (inclusive)
 * @property end The end instant (inclusive)
 * @throws IllegalArgumentException if start is not before end
 */
data class DateTimeRange(
    val start: Instant,
    val end: Instant
) : ValueObject {

    init {
        require(start.isBefore(end)) {
            "Start time must be before end time. Start: $start, End: $end"
        }
    }

    /**
     * Returns the duration of this time interval.
     *
     * @return Duration between start and end
     */
    fun duration(): Duration = Duration.between(start, end)

    /**
     * Checks if an instant falls within this time interval (inclusive).
     *
     * @param instant The instant to check
     * @return true if the instant is within the interval (inclusive)
     */
    fun contains(instant: Instant): Boolean {
        return !instant.isBefore(start) && !instant.isAfter(end)
    }

    /**
     * Checks if this time interval overlaps with another interval.
     *
     * Two intervals overlap if there is any instant that belongs to both.
     *
     * @param other The other time interval
     * @return true if the intervals overlap
     */
    fun overlaps(other: DateTimeRange): Boolean {
        return start.isBefore(other.end) && end.isAfter(other.start)
    }

    /**
     * Checks if this interval is completely within another interval.
     *
     * @param other The containing interval
     * @return true if this interval is completely within the other
     */
    fun isWithin(other: DateTimeRange): Boolean {
        return !start.isBefore(other.start) && !end.isAfter(other.end)
    }

    /**
     * Checks if this interval completely contains another interval.
     *
     * @param other The contained interval
     * @return true if this interval completely contains the other
     */
    fun contains(other: DateTimeRange): Boolean {
        return !other.start.isBefore(start) && !other.end.isAfter(end)
    }

    companion object {
        /**
         * Creates an interval starting now and extending for a given duration.
         *
         * @param duration The duration of the interval
         * @return A new DateTimeRange starting from now
         */
        fun fromNow(duration: Duration): DateTimeRange {
            val now = Instant.now()
            return DateTimeRange(now, now.plus(duration))
        }

        /**
         * Creates an interval of a specific duration starting at an instant.
         *
         * @param start The start instant
         * @param duration The duration of the interval
         * @return A new DateTimeRange with the specified start and duration
         */
        fun of(start: Instant, duration: Duration): DateTimeRange {
            return DateTimeRange(start, start.plus(duration))
        }
    }
}
