package com.rodkrtz.foundationkit.repository

import com.rodkrtz.foundationkit.aggregate.AggregateId
import com.rodkrtz.foundationkit.aggregate.AggregateRoot

/**
 * Repository interface for aggregate persistence.
 *
 * Repositories provide collection-like semantics for accessing aggregates,
 * abstracting the underlying persistence mechanism. They are responsible for:
 * - Saving and retrieving aggregates
 * - Maintaining aggregate consistency
 * - Publishing domain events after persistence
 * - Managing aggregate lifecycle
 *
 * Key principles:
 * - Repository per aggregate root (not per entity)
 * - Interface defined in domain layer
 * - Implementation in infrastructure layer
 * - Operations use domain language (not CRUD)
 *
 * @param T The aggregate root type
 * @param ID The aggregate identifier type
 */
public interface DomainRepository<T : AggregateRoot<ID>, ID : AggregateId<*>> {

    /**
     * Persists an aggregate.
     *
     * Can be used for both insert and update operations.
     * After saving, domain events should be published and cleared.
     *
     * @param aggregate The aggregate to persist
     * @return The persisted aggregate (may include generated values)
     */
    public fun save(aggregate: T): T

    /**
     * Finds an aggregate by its identifier.
     *
     * @param id The aggregate identifier
     * @return The aggregate if found, null otherwise
     */
    public fun findById(id: ID): T?

    /**
     * Retrieves all aggregates.
     *
     * Warning: This can return a large number of aggregates. Consider using
     * pagination or specific query methods for production use.
     *
     * @return List of all aggregates
     */
    public fun findAll(): List<T>

    /**
     * Deletes an aggregate by its identifier.
     *
     * Note: Consider using soft delete (marking as deleted) instead of
     * physical deletion to maintain audit trail.
     *
     * @param id The identifier of the aggregate to delete
     */
    public fun delete(id: ID)

    /**
     * Checks if an aggregate exists with the given identifier.
     *
     * @param id The identifier to check
     * @return true if an aggregate exists, false otherwise
     */
    public fun existsById(id: ID): Boolean
}
