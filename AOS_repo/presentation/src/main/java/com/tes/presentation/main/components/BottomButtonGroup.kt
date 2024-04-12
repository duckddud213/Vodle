package com.tes.presentation.main.components

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tes.presentation.R
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.fetchLocationAndHandle
import kotlinx.coroutines.CoroutineScope
import main.components.BottomNavBarComponent

@Composable
internal fun BottomButtonGroup(
    viewModel: MainViewModel,
    context: Context,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
    onClickUserButton: () -> Unit
) {
    BottomNavBarComponent(
        modifier = modifier.then(
            Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        ),
        onClickWriteButton = {
            fetchLocationAndHandle(
                scope = scope,
                context = context,
                onSuccess = { location ->
                    viewModel.onTriggerEvent(MainViewEvent.OnClickWriteButton(location))
                },
                onFailure = {
                    viewModel.onTriggerEvent(
                        MainViewEvent.ShowToast(context.getString(R.string.location_fetch_failure))
                    )
                }
            )
        },
        onClickHeadphoneButton = { /*TODO*/ },
        onClickRecordingButton = {
            fetchLocationAndHandle(
                scope = scope,
                context = context,
                onSuccess = { location ->
                    viewModel.onTriggerEvent(MainViewEvent.OnClickRecordingButton(location))
                },
                onFailure = {
                    viewModel.onTriggerEvent(
                        MainViewEvent.ShowToast(context.getString(R.string.location_fetch_failure))
                    )
                }
            )
        },
        onClickUserButton = { onClickUserButton() }
    )
}
