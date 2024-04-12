package com.tes.domain.usecase.vodle

import com.tes.domain.repository.VodleRepository
import javax.inject.Inject

class ConvertTTSUseCase @Inject constructor(
    private val vodleRepository: VodleRepository
) {
    suspend operator fun invoke(text: String, selectedVoice: String) =
        vodleRepository.convertTTS(text, selectedVoice)
}
