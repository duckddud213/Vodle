package com.tes.presentation.main.components

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naver.maps.map.compose.CameraPositionState
import com.tes.presentation.R
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.fetchLocationAndHandle
import com.tes.presentation.main.moveCameraPosition
import kotlinx.coroutines.CoroutineScope
import main.components.FabComponent

@Composable
internal fun CurrentLocationButton(
    viewModel: MainViewModel,
    context: Context,
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState,
    modifier: Modifier = Modifier
) {
    FabComponent(
        modifier = modifier.then(Modifier.padding(top = 12.dp, end = 12.dp)),
        imageVector = Icons.Outlined.MyLocation,
        onClick = {
            fetchLocationAndHandle(
                scope,
                context,
                onSuccess = { location ->
                    moveCameraPosition(cameraPositionState, location)
                },
                onFailure = {
                    viewModel.onTriggerEvent(
                        MainViewEvent.ShowToast(context.getString(R.string.location_fetch_failure))
                    )
                }
            )
        },
        info = ""
    )
}
