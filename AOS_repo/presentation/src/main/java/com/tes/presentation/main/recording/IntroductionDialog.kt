package com.tes.presentation.main.recording

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.tes.presentation.main.MainViewEvent
import com.tes.presentation.main.MainViewModel
import com.tes.presentation.main.MainViewState
import com.tes.presentation.model.VodleOption
import com.tes.presentation.model.VoiceType
import com.tes.presentation.utils.MediaPlayer

@Composable
internal fun IntroDuctionDialog(viewModel: MainViewModel, viewState: MainViewState.MakingVodle) {
    val context = LocalContext.current
    val selectedVoiceIndex = remember { mutableIntStateOf(0) }
    val selectedGenderState = remember { mutableStateOf(viewState.gender) }
    val voiceTypeList: List<VoiceType>

    when (viewState.vodleOption) {
        VodleOption.TEXT -> {
            voiceTypeList = VoiceType.entries.filter { it != VoiceType.ORIGINAL }
            TextIntroductionView(
                viewModel,
                selectedVoiceIndex,
                voiceTypeList,
                context
            )
        }

        VodleOption.VOICE -> {
            voiceTypeList = VoiceType.entries

            VoiceIntroductionView(
                viewModel,
                selectedGenderState,
                selectedVoiceIndex,
                voiceTypeList,
                context
            )
        }
    }

    LaunchedEffect(selectedVoiceIndex.intValue) {
        viewModel.onTriggerEvent(
            MainViewEvent.OnSelectVoiceType(voiceTypeList[selectedVoiceIndex.intValue])
        )
    }

    LaunchedEffect(viewState.selectedVoiceType) {
        when (viewState.selectedVoiceType) {
            VoiceType.ORIGINAL -> {
                MediaPlayer.stopSample()
            }

            else -> MediaPlayer.playSample(context, viewState.selectedVoiceType)
        }
    }

    LaunchedEffect(selectedGenderState.value) {
        viewModel.onTriggerEvent(
            MainViewEvent.OnSelectVoiceType(voiceTypeList[selectedVoiceIndex.intValue])
        )
    }
}
