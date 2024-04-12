package com.tes.vodle.repository

import com.tes.domain.TokenManager
import com.tes.domain.model.Location
import com.tes.domain.model.Vodle
import com.tes.domain.repository.UserRepository
import com.tes.vodle.datasource.user.UserDataSource
import com.tes.vodle.util.calculateHmac
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val tokenManager: TokenManager
) : UserRepository {

    override suspend fun signInNaver(code: String): Result<Unit> {
        val signature = calculateHmac("$code&NAVER")
        val result = runBlocking { userDataSource.signInNaver(code, signature, "NAVER") }
        return result.fold(
            onSuccess = {
                tokenManager.saveToken(it.data.accessToken, it.data.refreshToken)
                Result.success(Unit)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    override suspend fun getNaverId(accessToken: String): Result<String> =
        userDataSource.getNaverLoginId(accessToken).fold(
            onSuccess = {
                Result.success(it)
            },
            onFailure = {
                Result.failure(it)
            }
        )

    override suspend fun logout(): Result<Unit> {
        tokenManager.deleteTokens()
        return Result.success(Unit)
    }

    override suspend fun signOutWithNaver(
        naverClientId: String,
        naverSecret: String,
        accessToken: String
    ): Result<Unit> =
        userDataSource.signOutWithNaver(naverClientId, naverSecret, accessToken).fold(
            onSuccess = {
                tokenManager.deleteTokens()
                Result.success(Unit)
            },
            onFailure = {
                Result.failure(it)
            }
        )

    override suspend fun fetchMyVodle(): Result<List<Vodle>> =
        userDataSource.fetchMyVodle().fold(
            onSuccess = { response ->
                Result.success(
                    response.dataBody.map {
                        Vodle(
                            it.id,
                            it.date,
                            it.address,
                            it.writer,
                            it.category,
                            location = Location(it.latitude.toDouble(), it.longitude.toDouble()),
                            it.streamingURL
                        )
                    }
                )
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun autoLogin(): Result<Unit> {
        val accessToken = tokenManager.getAccessToken()
        val result = runBlocking { userDataSource.autoLogin("Bearer $accessToken") }

        return result.fold(
            onSuccess = {
                tokenManager.saveToken(it.data.accessToken, it.data.refreshToken)
                Result.success(Unit)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    override suspend fun checkAccessToken(): Result<Boolean> {
        val accessToken = tokenManager.getAccessToken()

        val result = runBlocking {
            if (accessToken == "") {
                Result.failure(exception = NullPointerException())
            } else {
                Result.success(accessToken)
            }
        }

        return result.fold(
            onSuccess = {
                Result.success(true)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}
