package com.tes.presentation.utils

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal fun distanceCalculator(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Boolean {
    val earthRadius = 6371 // 지구의 반지름 (단위: km)

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
        sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = earthRadius * c

    return distance < 0.05
}
