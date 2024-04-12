package com.tes.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tes.presentation.model.Location
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LocationModule {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    suspend fun getLocation(context: Context): Result<Location> = suspendCoroutine { continuation ->
        if (!LocationModule::fusedLocationClient.isInitialized) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 요청 필요
            continuation.resume(Result.failure(Exception("Location permission not granted")))
            return@suspendCoroutine
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val resultLocation = Location(location.latitude, location.longitude)
                continuation.resume(Result.success(resultLocation))
            } else {
                continuation.resume(Result.failure(Exception("Location is null")))
            }
        }.addOnFailureListener { exception ->
            continuation.resume(Result.failure(exception))
        }
    }
}
