package com.androidghost77.service

import com.androidghost77.model.UpdateEvent

interface UpdateQueueService {

    fun sendUpdateEvent(event: UpdateEvent)
}
