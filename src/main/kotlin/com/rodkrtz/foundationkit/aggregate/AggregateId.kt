package com.rodkrtz.foundationkit.aggregate

import java.io.Serializable

/**
 * Base class for aggregate identifiers.
 *
 * An aggregate identifier is a value object that uniquely identifies an aggregate
 * within the system. It encapsulates the identifier value and provides value-based
 * equality semantics.
 *
 * While technically a value object, AggregateId is kept in the aggregate package
 * due to its close relationship with aggregates and entities.
 *
 * Example usage:
 * ```kotlin
 * // UUID-based ID
 * data class UserId(override val value: UUID) : AggregateId<UUID>(value) {
 *     companion object {
 *         fun generate() = UserId(UUID.randomUUID())
 *         fun fromString(id: String) = UserId(UUID.fromString(id))
 *     }
 * }
 *
 * // String-based ID
 * data class OrderId(override val value: String) : AggregateId<String>(value) {
 *     companion object {
 *         fun generate() = OrderId("ORD-${UUID.randomUUID()}")
 *     }
 * }
 *
 * // Long-based ID (for auto-increment databases)
 * data class ProductId(override val value: Long) : AggregateId<Long>
 *
 * // Usage
 * val userId1 = UserId.generate()
 * val userId2 = UserId.fromString("550e8400-e29b-41d4-a716-446655440000")
 * 
 * println(userId1 == userId2) // false
 * println(userId1.value)      // UUID object
 * println(userId1.toString()) // "550e8400-e29b-41d4-a716-446655440000"
 *
 * // Type safety
 * fun findUser(userId: UserId) { /* ... */ }
 * findUser(userId1) // OK
 * // findUser(orderId) // Compile error! Type safety
 * ```
 *
 * @property value The actual identifier value (e.g., UUID, String, Long)
 */
public interface AggregateId<T> : Serializable {
    public val value: T
}
