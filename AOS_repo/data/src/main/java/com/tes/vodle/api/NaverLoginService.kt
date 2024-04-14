package com.tes.vodle.api

import com.tes.vodle.model.user.response.NaverUserIdResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface NaverLoginService {
    @GET("nid/me")
    suspend fun getNaverUserId(
        @Header("Authorization") accessToken: String
    ): NaverUserIdResponse
}
