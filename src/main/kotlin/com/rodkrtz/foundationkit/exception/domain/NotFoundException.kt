package com.rodkrtz.foundationkit.exception.domain

/**
 * Base exception for "not found" errors.
 *
 * This exception is thrown when a requested resource or entity cannot be found.
 * It's useful for distinguishing "not found" from other types of failures,
 * allowing specific handling (e.g., returning HTTP 404).
 *
 * Subclasses can provide more specific "not found" scenarios.
 *
 * Usage example:
 * ```
 * throw NotFoundException("User with email user@example.com not found")
 * ```
 *
 * @param message Description of what was not found
 */
public open class NotFoundException(
    message: String
) : DomainException(message)