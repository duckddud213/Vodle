package com.tes.vodle.model.user.response

data class
TokenResponse(
    val status: Int,
    val message: String,
    val data: Tokens
)

data class Tokens(
    val accessToken: String,
    val refreshToken: String
)
