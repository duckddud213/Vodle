package com.tes.vodle.api

import com.tes.vodle.model.user.response.NaverSignOutResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverAuthService {
    @GET("token")
    suspend fun signOutWithNaver(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("access_token") accessToken: String,
        @Query("grant_type") grantType: String = "delete",
        @Query("service_provider") serviceProvider: String = "Naver"
    ): NaverSignOutResponse
}
