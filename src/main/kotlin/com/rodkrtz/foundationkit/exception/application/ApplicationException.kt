package com.rodkrtz.foundationkit.exception.application

/**
 * Base exception for application layer errors.
 *
 * Application exceptions represent errors in use case orchestration, command/query validation,
 * transaction management, and other application-level concerns.
 *
 * These exceptions should NOT be used for:
 * - Domain business rule violations (use DomainException)
 * - Infrastructure technical errors (use InfrastructureException)
 *
 * @property message Error message describing what went wrong
 * @property cause Original exception that caused this error, if any
 *
 * Example usage:
 * ```kotlin
 * class PlaceOrderHandler {
 *     fun handle(command: PlaceOrderCommand): Order {
 *         if (command.items.isEmpty()) {
 *             throw CommandValidationException(
 *                 message = "Command validation failed",
 *                 commandType = "PlaceOrderCommand",
 *                 errors = mapOf("items" to listOf("Cannot be empty"))
 *             )
 *         }
 *         // ...
 *     }
 * }
 * ```
 */
abstract class ApplicationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)