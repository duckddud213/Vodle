package com.tes.presentation.main.components

import androidx.compose.runtime.Composable
import com.naver.maps.map.compose.CameraPositionState
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.MainViewState
import com.tes.presentation.model.Vodle
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun VodleMapClustering(
    viewModel: MainViewModel,
    viewState: MainViewState,
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState
) {
    VodleMapClustering(viewModel, viewState.vodleList, scope, cameraPositionState)
}

@Composable
private fun VodleMapClustering(
    viewModel: MainViewModel,
    vodleList: List<Vodle>,
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState
) {
    VodleMap(viewModel, vodleList, scope, cameraPositionState)
}
