package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Exception thrown when event store operation fails.
 *
 * @property streamId The event stream identifier
 * @property operation The event store operation (e.g., "append", "read", "subscribe")
 *
 * Example:
 * ```kotlin
 * try {
 *     eventStore.append(streamId, events)
 * } catch (e: Exception) {
 *     throw EventStoreException(
 *         message = "Failed to append events to stream",
 *         streamId = streamId,
 *         operation = "append",
 *         cause = e
 *     )
 * }
 * ```
 */
class EventStoreException(
    message: String,
    val streamId: String,
    val operation: String,
    cause: Throwable? = null
) : InfrastructureException(message, cause)