package com.rodkrtz.foundationkit.event

import java.time.Instant
import java.util.UUID

/**
 * Marker interface for domain events.
 *
 * Domain events represent something that happened in the domain that domain experts
 * care about. They capture the intent of a business operation and can trigger side
 * effects in other parts of the system.
 *
 * Characteristics of domain events:
 * - Immutable (cannot be changed after creation)
 * - Named in past tense (e.g., UserCreated, OrderPlaced)
 * - Contain all relevant data about what happened
 * - Timestamped with when they occurred
 *
 * Note: Default implementations of occurredOn and eventId regenerate values on each
 * access. Implementations should override with stored values.
 *
 * Examples: UserRegistered, OrderPlaced, PaymentProcessed, RideCompleted
 */
public interface DomainEvent {
    /**
     * The timestamp when the event occurred.
     * WARNING: Default implementation generates current time on each access.
     * Override with a stored value in implementations.
     *
     * @return The instant when the event occurred
     */
    public val occurredOn: Instant
        get() = Instant.now()

    /**
     * Unique identifier for this event instance.
     * WARNING: Default implementation generates a new UUID on each access.
     * Override with a stored value in implementations.
     *
     * @return Unique event identifier
     */
    public val eventId: String
        get() = UUID.randomUUID().toString()
}
