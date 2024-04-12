package com.tes.vodle.datasource.user

import com.tes.vodle.model.user.response.MyVodleResponse
import com.tes.vodle.model.user.response.TokenResponse

interface UserDataSource {

    suspend fun getNaverLoginId(accessToken: String): Result<String>

    suspend fun signInNaver(
        userCode: String,
        signature: String,
        provider: String
    ): Result<TokenResponse>

    suspend fun signOutWithNaver(
        naverClientId: String,
        naverSecret: String,
        accessToken: String
    ): Result<Unit>

    suspend fun fetchMyVodle(): Result<MyVodleResponse>

    suspend fun autoLogin(accessToken: String): Result<TokenResponse>
}
