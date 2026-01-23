package com.rodkrtz.foundationkit.aggregate

import com.rodkrtz.foundationkit.exception.domain.ConcurrencyException

/**
 * Aggregate root with optimistic locking support.
 *
 * This class extends AggregateRoot with version control to prevent concurrent
 * modification conflicts. Each update increments the version number, and
 * concurrent updates are detected by comparing expected vs actual versions.
 *
 * Optimistic locking pattern:
 * 1. Load aggregate (version = 1)
 * 2. User A and User B both load the same aggregate
 * 3. User A modifies and saves (version becomes 2)
 * 4. User B tries to save with version 1
 * 5. System detects conflict and throws ConcurrencyException
 *
 * Usage example:
 * ```
 * val order = repository.findById(orderId) // version = 1
 * order.addItem(item)
 * order.checkVersion(1) // Verify no one else updated it
 * repository.save(order) // version becomes 2
 * ```
 *
 * @param ID The type of the aggregate identifier
 */
public abstract class VersionedAggregateRoot<ID : AggregateId<*>> : AggregateRoot<ID>() {

    /**
     * Current version number for optimistic locking.
     *
     * Starts at 1 and increments with each update.
     */
    public abstract val version: Long

    /**
     * Increments and returns the next version number.
     *
     * Should be called when persisting updates to the aggregate.
     *
     * @return The next version number
     */
    protected fun incrementVersion(): Long = version + 1

    /**
     * Checks if the current version matches the expected version.
     *
     * Used to detect concurrent modifications. Throws an exception if
     * the versions don't match, indicating another process has modified
     * the aggregate since it was loaded.
     *
     * @param expectedVersion The version that was loaded
     * @throws ConcurrencyException if versions don't match
     */
    public fun checkVersion(expectedVersion: Long) {
        if (version != expectedVersion) {
            throw ConcurrencyException(
                aggregateId = id.toString(),
                expectedVersion = expectedVersion,
                actualVersion = version
            )
        }
    }
}
