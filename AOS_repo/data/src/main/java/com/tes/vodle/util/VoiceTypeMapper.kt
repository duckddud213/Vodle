package com.tes.vodle.util

import com.tes.domain.model.VoiceType

fun String.toVoiceType(): VoiceType =
    when (this) {
        "ahri" -> VoiceType.AHRI
        "optimusPrime" -> VoiceType.OPTIMUSPRIME
        "mundo" -> VoiceType.MUNDO
        "elsa" -> VoiceType.ELSA
        "trump" -> VoiceType.TRUMP
        else -> VoiceType.ORIGINAL
    }
