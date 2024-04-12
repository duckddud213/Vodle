package com.tes.vodle.model.vodle

import com.google.gson.annotations.SerializedName

data class ConversionResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message")val message: String,
    @SerializedName("data")val data: ConversionData
)

data class ConversionData(
    @SerializedName("convertedFileUrl")val convertedFileUrl: String,
    @SerializedName("selectedVoice")val selectedVoice: String
)
