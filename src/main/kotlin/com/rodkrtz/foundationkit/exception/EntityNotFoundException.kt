package com.rodkrtz.foundationkit.exception

/**
 * Exception thrown when an entity cannot be found by its identifier.
 *
 * This is a specialized NotFoundException that includes entity type and ID
 * information, making it easier to:
 * - Generate user-friendly error messages
 * - Log structured error information
 * - Handle specific entity types differently
 *
 * Usage examples:
 * ```
 * // Using secondary constructor with automatic message
 * throw EntityNotFoundException("User", "user-123")
 * // Message: "User with id user-123 not found"
 *
 * // Using primary constructor with custom message
 * throw EntityNotFoundException(
 *     message = "The requested user could not be found",
 *     entityType = "User",
 *     entityId = "user-123"
 * )
 * ```
 *
 * @param message Custom error message
 * @param entityType Optional type name of the entity (e.g., "User", "Order")
 * @param entityId Optional identifier that was not found
 */
class EntityNotFoundException(
    message: String,
    val entityType: String? = null,
    val entityId: String? = null
) : NotFoundException(message) {

    /**
     * Convenience constructor that generates a standard message.
     *
     * @param entityType The type name of the entity
     * @param entityId The identifier that was not found
     */
    constructor(entityType: String, entityId: String) : this(
        message = "$entityType with id $entityId not found",
        entityType = entityType,
        entityId = entityId
    )
}
