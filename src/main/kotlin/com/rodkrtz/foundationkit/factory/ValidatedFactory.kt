package com.rodkrtz.foundationkit.factory

import com.rodkrtz.foundationkit.aggregate.AggregateRoot

/**
 * Abstract factory with built-in validation support.
 *
 * This class provides a template for creating aggregates with validation.
 * The creation process is split into two steps:
 * 1. validate() - Check preconditions and business rules
 * 2. build() - Construct the aggregate
 *
 * Validation happens automatically before building, ensuring only valid
 * aggregates are created.
 *
 * Example usage:
 * ```
 * class OrderFactory(
 *     private val items: List<OrderItem>,
 *     private val customerId: String
 * ) : ValidatedFactory<Order>() {
 *
 *     override fun validate() {
 *         require(items.isNotEmpty()) { "Order must have at least one item" }
 *         require(customerId.isNotBlank()) { "Customer ID is required" }
 *     }
 *
 *     override fun build(): Order {
 *         return Order(
 *             id = OrderId.generate(),
 *             customerId = customerId,
 *             items = items
 *         )
 *     }
 * }
 *
 * val order = OrderFactory(items, customerId).create()
 * ```
 *
 * @param T The type of aggregate root to create
 */
public abstract class ValidatedFactory<T : AggregateRoot<*>> : Factory<T> {

    /**
     * Validates preconditions for aggregate creation.
     *
     * Should throw exceptions if validation fails:
     * - IllegalArgumentException for invalid parameters
     * - ValidationException for business rule violations
     * - BusinessRuleException for domain rule violations
     *
     * @throws Exception if validation fails
     */
    protected abstract fun validate()

    /**
     * Builds the aggregate after successful validation.
     *
     * This method is only called if validate() completes without exceptions.
     *
     * @return A new, fully initialized aggregate root
     */
    protected abstract fun build(): T

    /**
     * Creates the aggregate by validating first, then building.
     *
     * @return A new, validated aggregate root
     * @throws Exception if validation fails
     */
    override fun create(): T {
        validate()
        return build()
    }
}
