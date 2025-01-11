package com.androidghost77.service

import com.androidghost77.constants.ShowType
import java.io.File

interface ChecksumCalculator {
    fun calculateChecksum(file: File): String
    fun addFileToQueue(userName: String, file: File, id: String, type: ShowType)
}
