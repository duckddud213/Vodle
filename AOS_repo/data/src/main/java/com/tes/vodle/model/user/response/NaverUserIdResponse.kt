package com.tes.vodle.model.user.response

data class NaverUserIdResponse(
    val resultcode: String,
    val message: String,
    val response: NaverUserInfo
)

data class NaverUserInfo(
    val id: String
)
