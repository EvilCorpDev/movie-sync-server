package com.androidghost77.model

data class UpdateEvent(
    val fileName: String,
    val id: String,
    val type: String,
    val checksum: String,
    val username: String
)
