package com.rodkrtz.foundationkit.exception.application

import com.rodkrtz.foundationkit.exception.FoundationKitException

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
 */
abstract class ApplicationException(
    message: String,
    cause: Throwable? = null
) : FoundationKitException(message, cause)