package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Exception thrown when serialization or deserialization fails.
 *
 * @property dataType The type of data being serialized/deserialized
 * @property operation Either "serialization" or "deserialization"
 *
 * Example:
 * ```kotlin
 * try {
 *     val json = objectMapper.writeValueAsString(order)
 * } catch (e: JsonProcessingException) {
 *     throw SerializationException(
 *         message = "Failed to serialize order to JSON",
 *         dataType = "Order",
 *         operation = "serialization",
 *         cause = e
 *     )
 * }
 *
 * try {
 *     val order = objectMapper.readValue<Order>(json)
 * } catch (e: JsonProcessingException) {
 *     throw SerializationException(
 *         message = "Failed to deserialize JSON to Order",
 *         dataType = "Order",
 *         operation = "deserialization",
 *         cause = e
 *     )
 * }
 * ```
 */
class SerializationException(
    message: String,
    val dataType: String,
    val operation: String, // "serialization" or "deserialization"
    cause: Throwable? = null
) : InfrastructureException(message, cause) {

    override fun toString(): String =
        "SerializationException(dataType='$dataType', operation='$operation', message='$message')"
}