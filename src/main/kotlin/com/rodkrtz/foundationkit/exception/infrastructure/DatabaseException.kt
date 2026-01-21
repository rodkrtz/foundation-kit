package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Exception thrown when a database operation fails.
 *
 * This includes connection errors, query failures, constraint violations,
 * and other database-related issues.
 *
 * @property operation Optional description of the operation that failed (e.g., "save", "find", "delete")
 *
 * Example:
 * ```kotlin
 * try {
 *     connection.use { conn ->
 *         val stmt = conn.prepareStatement("INSERT INTO orders VALUES (?, ?)")
 *         stmt.executeUpdate()
 *     }
 * } catch (e: SQLException) {
 *     throw DatabaseException(
 *         message = "Failed to insert order into database",
 *         operation = "save",
 *         cause = e
 *     )
 * }
 * ```
 */
class DatabaseException(
    message: String,
    val operation: String? = null,
    cause: Throwable? = null
) : InfrastructureException(message, cause) {

    override fun toString(): String {
        val opStr = operation?.let { ", operation='$it'" } ?: ""
        return "DatabaseException(message='$message'$opStr, cause=${cause?.javaClass?.simpleName})"
    }
}