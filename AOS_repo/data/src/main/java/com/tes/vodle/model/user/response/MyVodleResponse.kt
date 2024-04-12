package com.tes.vodle.model.user.response

import com.google.gson.annotations.SerializedName
import com.tes.vodle.model.vodle.VodleResponse

data class MyVodleResponse(
    val status: Int,
    val message: String,
    @SerializedName("data") val dataBody: List<VodleResponse>
)
