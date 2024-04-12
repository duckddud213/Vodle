package com.tes.presentation.main.components

import android.widget.ImageView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapEffect
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.tes.presentation.R
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.fetchLocationAndHandle
import com.tes.presentation.model.Location
import com.tes.presentation.model.Vodle
import kotlinx.coroutines.CoroutineScope
import ted.gun0912.clustering.naver.TedNaverClustering

@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun VodleMap(
    viewModel: MainViewModel,
    vodleList: List<Vodle>,
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState
) {
    val locationSource = rememberFusedLocationSource()

    NaverMap(
        modifier = Modifier.fillMaxSize(),
        locationSource = locationSource,
        cameraPositionState = cameraPositionState,
        contentPadding = PaddingValues(48.dp),
        properties = MapProperties(
            locationTrackingMode = LocationTrackingMode.Face
        ),
        uiSettings = MapUiSettings(
            isLocationButtonEnabled = false,
            isZoomControlEnabled = false,
            isCompassEnabled = false

        )
    ) {
        val context = LocalContext.current
        var clusterManager by remember { mutableStateOf<TedNaverClustering<Vodle>?>(null) }
        var myLocation: Location

        MapEffect(vodleList) { map ->
            if (clusterManager == null) {
                clusterManager = TedNaverClustering.with<Vodle>(context, map)
                    .customMarker {
                        Marker().apply {
                            icon = OverlayImage.fromResource(R.drawable.megaphone)
                            width = 120
                            height = 120
                        }
                    }
                    .customCluster {
                        ImageView(context).apply {
                            setImageResource(R.drawable.resized_megaphone)
                        }
                    }.clusterClickListener {
                        fetchLocationAndHandle(
                            scope,
                            context,
                            onSuccess = { location ->
                                myLocation = location
                                viewModel.onTriggerEvent(
                                    MainViewEvent.OnClickMarker(
                                        myLocation,
                                        it.items.map { vodle -> vodle.location }
                                    )
                                )
                            },
                            onFailure = {
                                viewModel.onTriggerEvent(
                                    MainViewEvent.ShowToast(
                                        context.getString(R.string.location_fetch_failure)
                                    )
                                )
                            }
                        )
                    }
                    .markerClickListener {
                        fetchLocationAndHandle(
                            scope,
                            context,
                            onSuccess = { location ->
                                myLocation = location
                                viewModel.onTriggerEvent(
                                    MainViewEvent.OnClickMarker(
                                        myLocation,
                                        listOf(it.location)
                                    )
                                )
                            },
                            onFailure = {
                                viewModel.onTriggerEvent(
                                    MainViewEvent.ShowToast(
                                        context.getString(R.string.location_fetch_failure)
                                    )
                                )
                            }
                        )
                    }
                    .clusterAnimation(true)
                    .clickToCenter(true)
                    .make()
            }
            clusterManager?.clearItems()
            clusterManager?.addItems(vodleList)
        }
    }
}
