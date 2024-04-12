package com.tes.domain.model

data class Vodle(
    val id: Long,
    val date: String,
    val address: String,
    val writer: String,
    val category: String,
    val location: Location,
    val streamingURL: String
)
