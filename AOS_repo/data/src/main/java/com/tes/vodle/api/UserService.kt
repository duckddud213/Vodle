package com.tes.vodle.api

import com.tes.vodle.model.user.response.MyVodleResponse
import retrofit2.http.GET

interface UserService {

    @GET("api/auth/me")
    suspend fun fetchMyVodle(): MyVodleResponse
}
