package com.rodkrtz.foundationkit.event

import com.rodkrtz.foundationkit.event.DomainEvent

/**
 * Event Store for persisting domain events.
 *
 * The Event Store provides permanent storage of all domain events, enabling:
 * - Event Sourcing: Reconstructing aggregate state from events
 * - Audit Trail: Complete history of all changes
 * - Temporal Queries: State at any point in time
 * - Event Replay: Rebuilding projections from events
 * - Debugging: Understanding what happened and when
 *
 * Events in the store are immutable and append-only. Events are never
 * updated or deleted, only appended.
 *
 * Usage example:
 * ```
 * // Save events after aggregate changes
 * val events = aggregate.getDomainEvents()
 * eventStore.save(events)
 * aggregate.clearDomainEvents()
 *
 * // Retrieve aggregate history
 * val history = eventStore.getEventsForAggregate(aggregateId)
 * val aggregate = AggregateRoot.fromEvents(history)
 * ```
 */
public interface EventStore {
    /**
     * Persists a single domain event.
     *
     * @param event The event to persist
     */
    public fun save(event: DomainEvent)

    /**
     * Persists multiple domain events in order.
     *
     * Default implementation saves events sequentially.
     * Can be overridden for batch/transactional optimizations.
     *
     * @param events The list of events to persist
     */
    public fun save(events: List<DomainEvent>) {
        events.forEach { save(it) }
    }

    /**
     * Retrieves all events for a specific aggregate.
     *
     * Events are returned in the order they occurred, allowing
     * reconstruction of the aggregate's state.
     *
     * @param aggregateId The unique identifier of the aggregate
     * @return List of events in chronological order
     */
    public fun getEventsForAggregate(aggregateId: String): List<DomainEvent>

    /**
     * Retrieves all events from the store.
     *
     * Warning: This can return a large number of events. Consider using
     * pagination or filtering for production use.
     *
     * @return List of all events in chronological order
     */
    public fun getAllEvents(): List<DomainEvent>
}
