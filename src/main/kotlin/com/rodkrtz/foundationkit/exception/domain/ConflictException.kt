package com.rodkrtz.foundationkit.exception.domain

/**
 * Exception thrown when an operation conflicts with current resource state.
 *
 * Typical use-cases:
 * - unique constraint violations
 * - duplicate registration attempts
 * - idempotency conflicts
 */
public open class ConflictException(message: String) : DomainException(message)

