package com.tes.vodle.model.vodle

data class VodlesAroundRequest(
    val centerLatitude: Double,
    val centerLongitude: Double,
    val northEastLatitude: Double,
    val northEastLongitude: Double,
    val southWestLatitude: Double,
    val southWestLongitude: Double
)
