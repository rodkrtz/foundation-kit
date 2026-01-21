package com.rodkrtz.foundationkit.event

import com.rodkrtz.foundationkit.event.DomainEvent

/**
 * Subscriber for domain events.
 *
 * Event subscribers react to specific types of domain events by executing
 * side effects such as:
 * - Updating read models or projections
 * - Sending notifications (email, SMS, push)
 * - Triggering external integrations
 * - Starting sagas or processes
 *
 * Subscribers should be idempotent to handle duplicate events safely.
 *
 * Example implementation:
 * ```
 * class SendWelcomeEmailSubscriber : EventSubscriber<UserRegisteredEvent> {
 *     override fun handle(event: UserRegisteredEvent) {
 *         emailService.sendWelcome(event.email)
 *     }
 *
 *     override fun isSubscribedTo(event: DomainEvent): Boolean {
 *         return event is UserRegisteredEvent
 *     }
 * }
 * ```
 *
 * @param T The specific type of domain event this subscriber handles
 */
interface EventSubscriber<T : DomainEvent> {
    /**
     * Handles a domain event of the subscribed type.
     *
     * @param event The event to handle
     */
    fun handle(event: T)

    /**
     * Checks if this subscriber is interested in the given event.
     *
     * @param event The event to check
     * @return true if this subscriber should handle the event
     */
    fun isSubscribedTo(event: DomainEvent): Boolean
}
