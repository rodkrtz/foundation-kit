package com.rodkrtz.foundationkit.exception.application

/**
 * Exception thrown when an event publication fails at the application layer.
 *
 * @property eventType The type of event that failed to publish
 *
 * Example:
 * ```kotlin
 * try {
 *     eventPublisher.publish(orderPlacedEvent)
 * } catch (e: Exception) {
 *     throw EventPublicationException(
 *         message = "Failed to publish event",
 *         eventType = "OrderPlacedEvent",
 *         cause = e
 *     )
 * }
 * ```
 */
public class EventPublicationException(
    message: String,
    public val eventType: String,
    cause: Throwable? = null
) : ApplicationException(message, cause)