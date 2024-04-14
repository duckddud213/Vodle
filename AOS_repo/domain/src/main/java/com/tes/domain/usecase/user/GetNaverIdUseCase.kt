package com.tes.domain.usecase.user

import com.tes.domain.repository.UserRepository
import javax.inject.Inject

class GetNaverIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(accessToken: String) = userRepository.getNaverId(accessToken)
}
