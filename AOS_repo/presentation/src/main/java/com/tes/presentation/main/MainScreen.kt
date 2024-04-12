package com.tes.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.naver.maps.map.compose.rememberCameraPositionState
import com.tes.presentation.R
import com.tes.presentation.main.components.BottomButtonGroup
import com.tes.presentation.main.components.CurrentLocationButton
import com.tes.presentation.main.components.LoadingScreen
import com.tes.presentation.main.components.SearchVodleButton
import com.tes.presentation.main.components.VodleDialog
import com.tes.presentation.main.components.VodleMapClustering
import com.tes.presentation.main.components.VodleSnackBarHost
import com.tes.presentation.main.recording.CreateVodleDialog
import com.tes.presentation.main.recording.IntroDuctionDialog
import com.tes.presentation.main.recording.RecordingDialog
import com.tes.presentation.main.recording.RecordingStep
import com.tes.presentation.main.recording.TypingDialog

@Composable
internal fun MainScreen(
    viewModel: MainViewModel = hiltViewModel<MainViewModel>(),
    onClickUserButton: () -> Unit
) {
    val viewState = viewModel.uiState.collectAsState().value

    val cameraPositionState = rememberCameraPositionState()

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val player = ExoPlayer.Builder(LocalContext.current).build()
    val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

    val snackBarHostState = remember { SnackbarHostState() }

    ObserveToastMessage(
        viewState = viewState,
        viewModel = viewModel,
        snackBarHostState = snackBarHostState
    )

    VodleMapClustering(viewModel, viewState, scope, cameraPositionState)

    Box {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchVodleButton(
                viewModel = viewModel,
                cameraPositionState = cameraPositionState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            CurrentLocationButton(
                viewModel = viewModel,
                context = context,
                scope = scope,
                cameraPositionState = cameraPositionState,
                modifier = Modifier
                    .align(Alignment.End)
            )

//        CalendarButton(
//            modifier = Modifier.align(Alignment.End)
//        )

            Spacer(modifier = Modifier.weight(1f))

            if (viewState is MainViewState.ShowRecordedVodle) {
                VodleDialog(
                    viewModel = viewModel,
                    viewState = viewState,
                    context = context,
                    scope = scope,
                    player = player,
                    dataSourceFactory = dataSourceFactory,
                    snackBarHostState = snackBarHostState
                )
            }

            BottomButtonGroup(
                viewModel = viewModel,
                context = context,
                scope = scope,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClickUserButton = onClickUserButton
            )
        }

        if (snackBarHostState.currentSnackbarData?.visuals?.message != context.getString(
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

    if (viewState is MainViewState.MakingVodle) {
        when (viewState.recordingStep) {
            RecordingStep.INTRODUCTION -> IntroDuctionDialog(viewModel, viewState)
            RecordingStep.RECORDING -> RecordingDialog(viewModel, viewState)
            RecordingStep.CREATE -> CreateVodleDialog(
                viewModel,
                viewState,
                player
            )

            RecordingStep.TYPING -> TypingDialog(viewModel = viewModel, viewState)
        }
    }

    if (viewState.isLoading) {
        LoadingScreen()
    }
}

@Composable
private fun ObserveToastMessage(
    viewState: MainViewState,
    snackBarHostState: SnackbarHostState,
    viewModel: MainViewModel
) {
    LaunchedEffect(key1 = viewState.toastMessage) {
        if (viewState.toastMessage.isNotEmpty()) {
            snackBarHostState.currentSnackbarData?.dismiss()
            snackBarHostState.showSnackbar(
                viewState.toastMessage,
                actionLabel = "확인",
                duration = SnackbarDuration.Short
            )
            viewModel.onTriggerEvent(MainViewEvent.OnFinishToast)
        }
    }
}
