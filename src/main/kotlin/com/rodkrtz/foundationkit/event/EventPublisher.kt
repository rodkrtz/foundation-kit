package com.rodkrtz.foundationkit.event

import com.rodkrtz.foundationkit.event.DomainEvent

/**
 * Publisher for domain events.
 *
 * The EventPublisher is responsible for dispatching domain events to all
 * registered subscribers. It acts as a mediator in the observer pattern.
 *
 * Events are typically published after aggregate changes have been persisted
 * to ensure consistency. Publishers can be:
 * - Synchronous: Immediate event processing
 * - Asynchronous: Queue-based processing
 * - Hybrid: Critical events synchronous, others async
 *
 * Example usage:
 * ```
 * val event = UserRegisteredEvent(userId, email)
 * eventPublisher.publish(event)
 * ```
 */
interface EventPublisher {
    /**
     * Publishes a single domain event to all subscribers.
     *
     * @param event The event to publish
     */
    fun publish(event: DomainEvent)

    /**
     * Publishes multiple domain events in order.
     *
     * Default implementation publishes events sequentially.
     * Can be overridden for batch publishing optimizations.
     *
     * @param events The list of events to publish
     */
    fun publish(events: List<DomainEvent>) {
        events.forEach { publish(it) }
    }
}
