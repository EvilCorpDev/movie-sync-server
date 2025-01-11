package com.androidghost77.service

import java.io.File

interface FileService {
    fun scanFolders()
    fun getFileById(id: String): File
}
