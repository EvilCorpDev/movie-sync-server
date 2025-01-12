package com.androidghost77.job

import com.androidghost77.service.FileService
import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@ApplicationScoped
class FileScannerJob @Inject constructor(
    private val fileService: FileService
) {

    @Scheduled(every = "{filesystem.scan.period}")
    fun scanForNewFiles() {
        fileService.scanFolders()
    }
}
