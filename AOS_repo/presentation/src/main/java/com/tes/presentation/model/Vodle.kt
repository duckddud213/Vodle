package com.tes.presentation.model

import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng

data class VodleForLog(
    val id: Long,
    val date: String,
    val address: String
)

data class VodleCluster(
    val vodleList: List<Vodle>
)

data class Vodle(
    val id: Long,
    val date: String,
    val address: String,
    val writer: String,
    val category: String,
    val location: Location,
    val streamingURL: String
) : TedClusterItem {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng(location.lat, location.lng)
    }
}
