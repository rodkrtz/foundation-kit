package com.rodkrtz.foundationkit.exception.domain

import com.rodkrtz.foundationkit.exception.FoundationKitException

/**
 * Base exception for all domain-related errors.
 *
 * This is the root of the domain exception hierarchy. All domain-specific
 * exceptions should extend this class to distinguish them from technical
 * or infrastructure exceptions.
 *
 * Domain exceptions represent:
 * - Business rule violations
 * - Invalid domain state
 * - Validation errors
 * - Consistency violations
 *
 * Unlike technical exceptions (IOException, SQLException), domain exceptions
 * are part of the ubiquitous language and should be meaningful to domain experts.
 *
 * @param message The error message describing what went wrong
 * @param cause Optional underlying cause (another exception)
 */
public abstract class DomainException(
    message: String,
    cause: Throwable? = null
) : FoundationKitException(message, cause)