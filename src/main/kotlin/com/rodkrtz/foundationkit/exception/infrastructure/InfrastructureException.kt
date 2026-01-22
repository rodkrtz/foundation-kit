package com.rodkrtz.foundationkit.exception.infrastructure

import com.rodkrtz.foundationkit.exception.FoundationKitException

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
 */
abstract class InfrastructureException(
    message: String,
    cause: Throwable? = null
) : FoundationKitException(message, cause)