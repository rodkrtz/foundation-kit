package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Exception thrown when repository operation fails.
 *
 * This is a more specific database exception for repository pattern implementations.
 *
 * @property entityType The type of entity (e.g., "Order", "User")
 * @property operation The repository operation (e.g., "save", "findById", "delete")
 *
 * Example:
 * ```kotlin
 * try {
 *     // database operation
 * } catch (e: SQLException) {
 *     throw RepositoryException(
 *         message = "Failed to save entity",
 *         entityType = "Order",
 *         operation = "save",
 *         cause = e
 *     )
 * }
 * ```
 */
public class RepositoryException(
    message: String,
    public val entityType: String,
    public val operation: String,
    cause: Throwable? = null
) : InfrastructureException(message, cause) {

    override fun toString(): String =
        "RepositoryException(entityType='$entityType', operation='$operation', message='$message')"
}