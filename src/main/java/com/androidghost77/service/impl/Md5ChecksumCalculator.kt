package com.androidghost77.service.impl

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import com.androidghost77.constants.ShowType
import com.androidghost77.model.UpdateEvent
import com.androidghost77.service.ChecksumCalculator
import com.androidghost77.service.FileListHolderService
import com.androidghost77.service.UpdateQueueService
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.nio.file.Files
import java.util.concurrent.Executors

@ApplicationScoped
class Md5ChecksumCalculator @Inject constructor(
    private val store: FileListHolderService,
    private val updateQueueService: UpdateQueueService
) : ChecksumCalculator {
    private val executor = Executors.newFixedThreadPool(2)

    override fun calculateChecksum(file: File): String =
        Files.newInputStream(file.toPath()).use { DigestUtils.md5Hex(it) }

    override fun addFileToQueue(userName: String, file: File, id: String, type: ShowType) {
        executor.execute {
            val checksum = calculateChecksum(file)
            store.updateChecksum(userName, id, type, checksum)
            updateQueueService.sendUpdateEvent(
                UpdateEvent(
                    file.name, id,
                    type.name, checksum,
                    userName
                )
            )
        }
    }
}
