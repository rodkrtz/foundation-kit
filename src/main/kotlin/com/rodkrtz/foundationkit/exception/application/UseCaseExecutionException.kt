package com.rodkrtz.foundationkit.exception.application

/**
 * Exception thrown when a use case or application service execution fails.
 *
 * This is a generic application exception for errors that don't fit into more specific
 * exception types.
 *
 * Example:
 * ```kotlin
 * throw UseCaseExecutionException(
 *     message = "Failed to process order placement",
 *     useCaseName = "PlaceOrderUseCase",
 *     cause = originalException
 * )
 * ```
 */
public class UseCaseExecutionException(
    message: String,
    public val useCaseName: String,
    cause: Throwable? = null
) : ApplicationException(message, cause)