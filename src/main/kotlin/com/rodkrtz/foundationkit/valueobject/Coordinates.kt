package com.rodkrtz.foundationkit.valueobject

import kotlin.math.*

/**
 * Represents a geographic coordinate with latitude and longitude.
 *
 * This value object encapsulates WGS84 coordinates and provides distance
 * calculations using the Haversine formula.
 *
 * @property latitude Latitude in degrees, must be between -90 and 90
 * @property longitude Longitude in degrees, must be between -180 and 180
 *
 * @throws IllegalArgumentException if latitude or longitude are out of valid range
 */
data class Coordinates(
    val latitude: Double,
    val longitude: Double
) : ValueObject {

    init {
        require(latitude in -90.0..90.0) {
            "Latitude must be between -90 and 90, got $latitude"
        }
        require(longitude in -180.0..180.0) {
            "Longitude must be between -180 and 180, got $longitude"
        }
    }

    /**
     * Calculates the distance to another coordinate using the Haversine formula.
     *
     * The Haversine formula determines the great-circle distance between two points
     * on a sphere given their longitudes and latitudes.
     *
     * @param other The target coordinate
     * @return Distance in kilometers
     */
    fun distanceTo(other: Coordinates): Double {
        val earthRadiusKm = 6371.0

        val dLat = (other.latitude - latitude).toRadians()
        val dLon = (other.longitude - longitude).toRadians()

        val lat1 = latitude.toRadians()
        val lat2 = other.latitude.toRadians()

        val a = sin(dLat / 2).pow(2) +
                sin(dLon / 2).pow(2) * cos(lat1) * cos(lat2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }

    /**
     * Checks if this coordinate is within a given radius from a center point.
     *
     * @param center The center coordinate
     * @param radiusKm The radius in kilometers
     * @return true if this coordinate is within the radius, false otherwise
     */
    fun isWithinRadius(center: Coordinates, radiusKm: Double): Boolean {
        return distanceTo(center) <= radiusKm
    }
    
    /**
     * Converts degrees to radians.
     */
    private fun Double.toRadians() = this * PI / 180.0
}
