package com.androidghost77.service.impl

import com.androidghost77.model.UpdateEvent
import com.androidghost77.service.UpdateQueueService
import com.fasterxml.jackson.databind.ObjectMapper
import org.jboss.logging.Logger
import software.amazon.awssdk.services.sqs.SqsClient

class SQSUpdateQueueService (
    val sqs: SqsClient,
    val queueUrl: String,
    val objectMapper: ObjectMapper
) : UpdateQueueService {

    val log: Logger = Logger.getLogger("SQSUpdateQueueServiceLogger")
    override fun sendUpdateEvent(event: UpdateEvent) {
        val eventStr = objectMapper.writeValueAsString(event)
        sqs.sendMessage{ msg -> msg.queueUrl(queueUrl).messageBody(eventStr) }
        log.infov("Sent update event {0} to sqs", eventStr)
    }
}
