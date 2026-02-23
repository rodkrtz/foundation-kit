package com.rodkrtz.foundationkit.valueobject

public data class LocationPoint(
    val address: String,
    var latitude: Double,
    var longitude: Double
) {
    init {
        require(latitude in -90.0..90.0) { "Invalid Latitude: $latitude" }
        require(longitude in -180.0..180.0) { "Invalid Longitude: $longitude" }
    }

    public fun rounded(decimals: Int): LocationPoint {
        return this.copy(
            latitude = this.latitude.round(decimals),
            longitude = this.longitude.round(decimals)
        )
    }

}