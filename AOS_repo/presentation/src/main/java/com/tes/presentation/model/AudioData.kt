package com.tes.presentation.model

data class AudioData(
    val voiceType: VoiceType = VoiceType.ORIGINAL,
    val convertedAudioUrl: String = ""
)

typealias Url = String
