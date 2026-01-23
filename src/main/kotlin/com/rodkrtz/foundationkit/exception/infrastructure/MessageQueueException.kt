package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Exception thrown when a message queue operation fails.
 *
 * @property queueName The name of the queue
 * @property operation The operation that failed (e.g., "publish", "consume", "ack")
 *
 * Example:
 * ```kotlin
 * try {
 *     messageQueue.publish(queueName, message)
 * } catch (e: Exception) {
 *     throw MessageQueueException(
 *         message = "Failed to publish message to queue",
 *         queueName = queueName,
 *         operation = "publish",
 *         cause = e
 *     )
 * }
 * ```
 */
public class MessageQueueException(
    message: String,
    public val queueName: String,
    public val operation: String,
    cause: Throwable? = null
) : InfrastructureException(message, cause)