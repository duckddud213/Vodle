package com.tes.domain.usecase.vodle

import com.tes.domain.model.Gender
import com.tes.domain.repository.VodleRepository
import java.io.File
import javax.inject.Inject

class ConvertRecordingUseCase @Inject constructor(
    private val vodleRepository: VodleRepository
) {
    suspend operator fun invoke(recordingFile: File, selectedVoice: String, gender: Gender) =
        vodleRepository.convertVoice(recordingFile, selectedVoice, gender)
}
