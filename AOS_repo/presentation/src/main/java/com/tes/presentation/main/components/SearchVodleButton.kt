package com.tes.presentation.main.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.naver.maps.map.compose.CameraPositionState
import com.tes.presentation.R
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.model.Location
import main.components.ButtonComponent

@Composable
internal fun SearchVodleButton(
    viewModel: MainViewModel,
    cameraPositionState: CameraPositionState,
    modifier: Modifier = Modifier
) {
    ButtonComponent(
        modifier = modifier.then(Modifier.padding(top = 12.dp)),
        buttonText = stringResource(R.string.search_in_this_location),
        onClick = {
            viewModel.onTriggerEvent(
                MainViewEvent.OnClickSearchVodleButton(
                    Location(
                        cameraPositionState.position.target.latitude,
                        cameraPositionState.position.target.longitude
                    ),
                    Location(
                        cameraPositionState.contentBounds!!.northLatitude,
                        cameraPositionState.contentBounds!!.eastLongitude
                    ),
                    Location(
                        cameraPositionState.contentBounds!!.southLatitude,
                        cameraPositionState.contentBounds!!.westLongitude
                    )
                )
            )
        },
        buttonTextStyle = TextStyle()
    )
}
