package com.androidghost77.model

import com.androidghost77.constants.ShowType

data class MovieInfo(
        val name: String,
        val id: String,
        val folder: String,
        val size: Long,
        val hashSum: String,
        val showType: ShowType,
        val owner: String,
)
