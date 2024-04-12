package com.tes.vodle.repository

import com.tes.domain.model.AudioData
import com.tes.domain.model.Gender
import com.tes.domain.model.Location
import com.tes.domain.model.RecordType
import com.tes.domain.model.Vodle
import com.tes.domain.repository.VodleRepository
import com.tes.vodle.datasource.vodle.VodleDataSource
import com.tes.vodle.util.toVoiceType
import java.io.File
import javax.inject.Inject

class VodleRepositoryImpl @Inject constructor(
    private val vodleDataSource: VodleDataSource
) : VodleRepository {
    override suspend fun fetchVodlesAround(
        centerLocation: Location,
        northEastLocation: Location,
        southWestLocation: Location
    ): Result<List<Vodle>> =
        vodleDataSource.fetchVodlesAround(
            centerLocation,
            northEastLocation,
            southWestLocation
        ).fold(
            onSuccess = { it ->
                val vodleList: List<Vodle> = it.dataBody.map {
                    Vodle(
                        it.id,
                        it.date,
                        it.address,
                        it.writer,
                        it.category,
                        Location(it.latitude.toDouble(), it.longitude.toDouble()),
                        it.streamingURL
                    )
                }

                Result.success(vodleList)
            },
            onFailure = { Result.failure(Exception()) }
        )

    override suspend fun uploadVodle(
        recordingFile: File,
        writer: String,
        recordType: RecordType,
        streamingUrl: String,
        location: Location
    ): Result<Unit> =
        vodleDataSource.uploadVodle(
            recordingFile,
            writer,
            recordType.name,
            streamingUrl,
            location
        ).fold(
            onSuccess = {
                Result.success(Unit)
            },
            onFailure = {
                Result.failure(Exception())
            }
        )

    override suspend fun convertVoice(
        recordingFile: File,
        selectedVoice: String,
        gender: Gender
    ): Result<AudioData> =
        vodleDataSource.convertVoice(recordingFile, selectedVoice, gender).fold(
            onSuccess = { it ->
                Result.success(
                    AudioData(
                        it.data.selectedVoice.toVoiceType(),
                        it.data.convertedFileUrl
                    )
                )
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )

    override suspend fun convertTTS(content: String, selectedVoice: String): Result<AudioData> =
        vodleDataSource.convertTTS(content, selectedVoice).fold(
            onSuccess = {
                Result.success(
                    AudioData(
                        it.data.selectedVoice.toVoiceType(),
                        it.data.convertedFileUrl
                    )
                )
            },
            onFailure = {
                Result.failure(it)
            }
        )
}
