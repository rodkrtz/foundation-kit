package com.rodkrtz.foundationkit.exception.domain

/**
 * Exception thrown when concurrent modification is detected.
 *
 * This exception is used in optimistic locking scenarios where multiple
 * processes attempt to modify the same aggregate simultaneously. It indicates
 * that the aggregate has been modified since it was loaded.
 *
 * Typical scenario:
 * 1. Process A loads Order with version 1
 * 2. Process B loads Order with version 1
 * 3. Process A modifies and saves → version becomes 2
 * 4. Process B tries to save with version 1 → ConcurrencyException!
 *
 * Handling strategies:
 * - Retry: Reload the aggregate and retry the operation
 * - Merge: Attempt to merge changes if possible
 * - Abort: Inform user of conflict and let them decide
 *
 * Usage example:
 * ```
 * if (aggregate.version != expectedVersion) {
 *     throw ConcurrencyException(
 *         message = "Order was modified by another user",
 *         aggregateId = order.id.toString(),
 *         expectedVersion = 1,
 *         actualVersion = 2
 *     )
 * }
 * ```
 *
 * @param message Description of the concurrency conflict
 * @param aggregateId Optional identifier of the aggregate that has a conflict
 * @param expectedVersion Optional version that was expected
 * @param actualVersion Optional actual current version
 */
public class ConcurrencyException(
    message: String = "Concurrent modification detected",
    public val aggregateId: String? = null,
    public val expectedVersion: Long? = null,
    public val actualVersion: Long? = null
) : DomainException(message)