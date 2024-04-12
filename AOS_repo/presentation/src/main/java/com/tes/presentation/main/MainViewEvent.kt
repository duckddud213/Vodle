package com.tes.presentation.main

import com.tes.domain.model.Gender
import com.tes.domain.model.RecordType
import com.tes.presentation.composebase.ViewEvent
import com.tes.presentation.model.Location
import com.tes.presentation.model.VodleOption
import com.tes.presentation.model.VoiceType
import java.io.File

sealed class MainViewEvent : ViewEvent {

    data class OnClickSearchVodleButton(
        val centerLocation: Location,
        val northEastLocation: Location,
        val southWestLocation: Location
    ) : MainViewEvent()

    data class OnClickWriteButton(
        val location: Location,
        val vodleOption: VodleOption = VodleOption.TEXT
    ) : MainViewEvent()

    data object OnClickHeadPhoneButton : MainViewEvent()

    data class OnClickRecordingButton(val location: Location) : MainViewEvent()

    data object OnDismissRecordingDialog : MainViewEvent()

    data object OnClickMakingVodleButton : MainViewEvent()
    data class OnClickFinishRecordingButton(
        val recordingFile: File,
        val selectedVoiceType: VoiceType,
        val gender: Gender
    ) : MainViewEvent()

    data class OnClickFinishTypingButton(
        val text: String,
        val selectedVoiceType: VoiceType
    ) : MainViewEvent()

    data class OnClickSaveVodleButton(
        val recordingFile: File,
        val writer: String,
        val recordType: RecordType,
        val streamingUrl: String,
        val location: Location
    ) : MainViewEvent()

    data class ShowToast(val message: String) : MainViewEvent()

    data object OnFinishToast : MainViewEvent()

    data class OnClickMarker(val myLocation: Location, val locationList: List<Location>) : MainViewEvent()

    data object OnDismissVodleDialog : MainViewEvent()

    data class OnFailMakingVodle(val toastMessage: String) : MainViewEvent()

    data class OnSelectVoiceType(val voiceType: VoiceType) : MainViewEvent()

    data class OnSelectGender(val gender: Gender) : MainViewEvent()

    data object OnFailStreaming : MainViewEvent()
}
