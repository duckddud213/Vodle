package com.tes.presentation.main.components

import android.content.Context
import android.view.Gravity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import com.tes.presentation.R
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.MainViewState
import kotlinx.coroutines.CoroutineScope
import main.components.VodleListDialogComponent

@Composable
internal fun VodleDialog(
    viewModel: MainViewModel,
    viewState: MainViewState.ShowRecordedVodle,
    context: Context,
    scope: CoroutineScope,
    player: ExoPlayer,
    dataSourceFactory: DataSource.Factory,
    snackBarHostState: SnackbarHostState
) {
    Dialog(
        onDismissRequest = {
            viewModel.onTriggerEvent(MainViewEvent.OnDismissVodleDialog)
            player.pause()
            player.stop()
        }
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0f)
        (LocalView.current.parent as DialogWindowProvider).window.setGravity(Gravity.BOTTOM)
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
            Column {
                VodleListDialogComponent(
                    viewModel,
                    viewState.dialogVodleList,
                    context,
                    scope,
                    player,
                    dataSourceFactory
                )
                Spacer(modifier = Modifier.height(90.dp))
            }

            if (snackBarHostState.currentSnackbarData?.visuals?.message == context.getString(
                    R.string.distance_limit_message
                )
            ) {
                VodleSnackBarHost(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    snackBarHostState = snackBarHostState
                )
            }
        }
    }
}
