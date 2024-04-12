package com.tes.vodle.datasource.user

import com.tes.vodle.api.AuthService
import com.tes.vodle.api.NaverAuthService
import com.tes.vodle.api.NaverLoginService
import com.tes.vodle.api.UserService
import com.tes.vodle.model.user.request.NaverLoginRequest
import com.tes.vodle.model.user.response.MyVodleResponse
import com.tes.vodle.model.user.response.TokenResponse
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val authService: AuthService,
    private val naverLoginService: NaverLoginService,
    private val naverAuthService: NaverAuthService,
    private val userService: UserService
) : UserDataSource {
    override suspend fun getNaverLoginId(accessToken: String): Result<String> = runCatching {
        naverLoginService.getNaverUserId("Bearer $accessToken").response.id
    }

    override suspend fun signInNaver(
        userCode: String,
        signature: String,
        provider: String
    ): Result<TokenResponse> = runCatching {
        authService.signInNaver(NaverLoginRequest(userCode, provider, signature))
    }

    override suspend fun signOutWithNaver(
        naverClientId: String,
        naverSecret: String,
        accessToken: String
    ): Result<Unit> = runCatching {
        naverAuthService.signOutWithNaver(
            naverClientId,
            naverSecret,
            accessToken
        )
    }

    override suspend fun fetchMyVodle(): Result<MyVodleResponse> = runCatching {
        userService.fetchMyVodle()
    }

    override suspend fun autoLogin(accessToken: String): Result<TokenResponse> = runCatching {
        authService.autoLogin(accessToken)
    }
}
