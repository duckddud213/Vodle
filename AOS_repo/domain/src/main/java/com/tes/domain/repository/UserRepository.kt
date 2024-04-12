package com.tes.domain.repository

import com.tes.domain.model.Vodle

interface UserRepository {

    suspend fun signInNaver(code: String): Result<Unit>

    suspend fun getNaverId(accessToken: String): Result<String>

    suspend fun logout(): Result<Unit>

    suspend fun signOutWithNaver(
        naverClientId: String,
        naverSecret: String,
        accessToken: String
    ): Result<Unit>

    suspend fun fetchMyVodle(): Result<List<Vodle>>

    suspend fun autoLogin(): Result<Unit>

    suspend fun checkAccessToken(): Result<Boolean>
}
