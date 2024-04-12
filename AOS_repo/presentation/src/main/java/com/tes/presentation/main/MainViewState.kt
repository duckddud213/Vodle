package com.tes.presentation.main

import com.tes.domain.model.Gender
import com.tes.presentation.composebase.ViewState
import com.tes.presentation.main.recording.RecordingStep
import com.tes.presentation.model.AudioData
import com.tes.presentation.model.Location
import com.tes.presentation.model.Vodle
import com.tes.presentation.model.VodleOption
import com.tes.presentation.model.VoiceType
import java.io.File

sealed class MainViewState : ViewState {
    abstract val vodleMap: HashMap<Location, List<Vodle>>
    abstract val isLoading: Boolean
    abstract val toastMessage: String
    abstract val vodleList: List<Vodle>

    data class Default(
        override val vodleMap: HashMap<Location, List<Vodle>> = HashMap<Location, List<Vodle>>(),
        override val toastMessage: String = "",
        override val vodleList: List<Vodle> = emptyList(),
        override val isLoading: Boolean = false
    ) : MainViewState()

    data class MakingVodle(
        override val vodleMap: HashMap<Location, List<Vodle>> = HashMap<Location, List<Vodle>>(),
        override val vodleList: List<Vodle> = emptyList(),
        override val toastMessage: String = "",
        val vodleOption: VodleOption = VodleOption.VOICE,
        val recordingStep: RecordingStep = RecordingStep.INTRODUCTION,
        val location: Location,
        val convertedAudio: AudioData,
        val recordingFile: File = File("none"),
        val selectedVoiceType: VoiceType = VoiceType.ORIGINAL,
        val gender: Gender = Gender.Male,
        override val isLoading: Boolean = false
    ) : MainViewState()

    data class ShowRecordedVodle(
        override val vodleMap: HashMap<Location, List<Vodle>> = HashMap<Location, List<Vodle>>(),
        override val toastMessage: String = "",
        override val vodleList: List<Vodle>,
        override val isLoading: Boolean = false,
        val dialogVodleList: List<Vodle>,
        val myLocation: Location
    ) : MainViewState()
}
