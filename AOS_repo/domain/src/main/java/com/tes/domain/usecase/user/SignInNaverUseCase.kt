package com.tes.domain.usecase.user

import com.tes.domain.repository.UserRepository
import javax.inject.Inject

class SignInNaverUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(code: String) = repository.signInNaver(code)
}
