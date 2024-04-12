package com.tes.domain.model

enum class VoiceType {
    AHRI, MUNDO, OPTIMUSPRIME, TRUMP, ELSA, ORIGINAL
}

enum class Gender(val value: String) {
    Male("male"), Female("female")
}

data class AudioData(
    val voiceType: VoiceType,
    val convertedAudioUrl: Url
)

typealias Url = String
