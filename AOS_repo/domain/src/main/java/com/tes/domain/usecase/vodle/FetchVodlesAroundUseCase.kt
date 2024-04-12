package com.tes.domain.usecase.vodle

import com.tes.domain.model.Location
import com.tes.domain.repository.VodleRepository
import javax.inject.Inject

class FetchVodlesAroundUseCase @Inject constructor(
    private val vodleRepository: VodleRepository
) {
    suspend operator fun invoke(
        centerLocation: Location,
        northEastLocation: Location,
        southWestLocation: Location
    ) = vodleRepository.fetchVodlesAround(
        centerLocation,
        northEastLocation,
        southWestLocation
    )
}
