package com.tes.domain.usecase.user

import com.tes.domain.repository.UserRepository
import javax.inject.Inject

class SignOutNaverUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        naverClientId: String,
        naverSecret: String,
        accessToken: String
    ) = userRepository.signOutWithNaver(
        naverClientId,
        naverSecret,
        accessToken
    )
}
