package com.tes.vodle.util

import com.tes.domain.TokenManager
import com.tes.vodle.api.AuthService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = runBlocking { tokenManager.getAccessToken() }
        val refreshToken = runBlocking { tokenManager.getRefreshToken() }

        if (refreshToken == "null") return null

        return runBlocking {
            val result = runCatching {
                authService.refreshUserToken(
                    refreshToken,
                    "Bearer $accessToken"
                )
            }

            if (result.isFailure || result.getOrNull() == null) {
                tokenManager.deleteTokens()
                null
            } else {
                val tokens = result.getOrThrow()
                tokenManager.saveToken(tokens.data.accessToken, tokens.data.refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokens.data.accessToken}")
                    .build()
            }
        }
    }
}
