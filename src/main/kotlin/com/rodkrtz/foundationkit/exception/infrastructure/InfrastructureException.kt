package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Base exception for infrastructure layer errors.
 *
 * Infrastructure exceptions represent technical errors such as database failures,
 * network issues, external service failures, and other infrastructure concerns.
 *
 * These exceptions should NOT be used for:
 * - Domain business rule violations (use DomainException)
 * - Application orchestration errors (use ApplicationException)
 *
 * @property message Error message describing what went wrong
 * @property cause Original exception that caused this error, if any
 *
 * Example usage:
 * ```kotlin
 * class PostgresOrderRepository : OrderRepository {
 *     override fun save(order: Order): Order {
 *         try {
 *             // database operations
 *             return order
 *         } catch (e: SQLException) {
 *             throw DatabaseException(
 *                 message = "Failed to save order to database",
 *                 cause = e
 *             )
 *         }
 *     }
 * }
 * ```
 */
abstract class InfrastructureException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)