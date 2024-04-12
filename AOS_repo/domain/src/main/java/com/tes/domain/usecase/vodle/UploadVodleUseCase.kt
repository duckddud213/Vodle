package com.tes.domain.usecase.vodle

import com.tes.domain.model.Location
import com.tes.domain.model.RecordType
import com.tes.domain.repository.VodleRepository
import java.io.File
import javax.inject.Inject

class UploadVodleUseCase @Inject constructor(
    private val vodleRepository: VodleRepository
) {
    suspend operator fun invoke(
        recordingFile: File,
        writer: String,
        recordType: RecordType,
        streamingUrl: String,
        location: Location
    ) = vodleRepository.uploadVodle(
        recordingFile,
        writer,
        recordType,
        streamingUrl,
        location
    )
}
