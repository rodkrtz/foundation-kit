package com.rodkrtz.foundationkit.aggregate

import com.rodkrtz.foundationkit.event.DomainEvent

/**
 * Base class for aggregate roots in Domain-Driven Design.
 *
 * An aggregate root is the entry point to an aggregate - a cluster of domain objects
 * that can be treated as a single unit for data changes. The aggregate root ensures
 * consistency of changes within the aggregate boundary.
 *
 * This class provides domain event management capabilities, allowing the aggregate
 * to record events that occurred during its lifecycle.
 *
 * Example usage:
 * ```kotlin
 * // Define aggregate ID
 * data class OrderId(override val value: UUID) : AggregateId<UUID>(value)
 *
 * // Define domain events
 * data class OrderPlacedEvent(
 *     override val eventId: String = UUID.randomUUID().toString(),
 *     override val occurredOn: Instant = Instant.now(),
 *     val orderId: UUID,
 *     val customerId: String,
 *     val totalAmount: Money
 * ) : DomainEvent
 *
 * data class OrderCancelledEvent(
 *     override val eventId: String = UUID.randomUUID().toString(),
 *     override val occurredOn: Instant = Instant.now(),
 *     val orderId: UUID,
 *     val reason: String
 * ) : DomainEvent
 *
 * // Define aggregate
 * class Order(
 *     override val id: OrderId,
 *     val customerId: String,
 *     val items: List<OrderItem>,
 *     var status: OrderStatus
 * ) : AggregateRoot<OrderId>() {
 *
 *     fun place() {
 *         require(status == OrderStatus.DRAFT) { "Only draft orders can be placed" }
 *         status = OrderStatus.PLACED
 *         addDomainEvent(OrderPlacedEvent(
 *             orderId = id.value,
 *             customerId = customerId,
 *             totalAmount = calculateTotal()
 *         ))
 *     }
 *
 *     fun cancel(reason: String) {
 *         require(status == OrderStatus.PLACED) { "Only placed orders can be cancelled" }
 *         status = OrderStatus.CANCELLED
 *         addDomainEvent(OrderCancelledEvent(
 *             orderId = id.value,
 *             reason = reason
 *         ))
 *     }
 *
 *     private fun calculateTotal(): Money = 
 *         items.map { it.price }.reduce { acc, money -> acc + money }
 * }
 *
 * // Usage
 * val order = Order(
 *     id = OrderId(UUID.randomUUID()),
 *     customerId = "customer-123",
 *     items = listOf(
 *         OrderItem(Product("Product A"), Money.of(100.0), quantity = 2)
 *     ),
 *     status = OrderStatus.DRAFT
 * )
 *
 * order.place()
 *
 * // Get events to publish
 * val events = order.getDomainEvents()
 * eventPublisher.publish(events)
 * order.clearDomainEvents()
 * ```
 *
 * @param ID The type of the aggregate identifier
 */
public abstract class AggregateRoot<ID : AggregateId<*>> : Entity<ID>() {

    /** List of domain events recorded but not yet published */
    private val domainEvents = mutableListOf<DomainEvent>()

    /**
     * Adds a domain event to be published later.
     *
     * Domain events represent something that happened in the domain that domain
     * experts care about. They are typically published after the aggregate is
     * successfully persisted.
     *
     * @param event The domain event to record
     */
    protected fun addDomainEvent(event: DomainEvent) {
        domainEvents.add(event)
    }

    /**
     * Gets all recorded domain events.
     *
     * Returns a copy of the internal list to prevent external modification.
     *
     * @return Immutable list of domain events
     */
    public fun getDomainEvents(): List<DomainEvent> = domainEvents.toList()

    /**
     * Clears all recorded domain events.
     *
     * Should be called after events have been successfully published to avoid
     * republishing the same events.
     */
    public fun clearDomainEvents() {
        domainEvents.clear()
    }
}
