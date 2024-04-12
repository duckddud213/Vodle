package com.tes.presentation.main

import android.content.Context
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.tes.presentation.model.Location
import com.tes.presentation.model.lat
import com.tes.presentation.model.lng
import com.tes.presentation.utils.LocationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalNaverMapApi::class)
internal fun moveCameraPosition(
    cameraPositionState: CameraPositionState,
    location: Location
) {
    cameraPositionState.move(
        CameraUpdate.toCameraPosition(
            CameraPosition(
                LatLng(location.lat, location.lng),
                16.0,
                45.0,
                0.0
            )
        )
    )
}

internal fun fetchLocationAndHandle(
    scope: CoroutineScope,
    context: Context,
    onSuccess: (Location) -> Unit,
    onFailure: () -> Unit
) {
    scope.launch(Dispatchers.IO) {
        val result = LocationModule.getLocation(context)
        if (result.isSuccess) {
            withContext(Dispatchers.Main) { onSuccess(result.getOrThrow()) }
        } else {
            withContext(Dispatchers.Main) { onFailure() }
        }
    }
}
