package com.rodkrtz.foundationkit.aggregate

/**
 * Base class for entities in Domain-Driven Design.
 *
 * An entity is an object that has a distinct identity that runs through time
 * and different states. Unlike value objects, two entities with different
 * identifiers are considered different even if all their attributes are identical.
 *
 * Equality is based solely on the identifier, not on the attributes.
 *
 * @param ID The type of the entity identifier (must extend AggregateId)
 */
public abstract class Entity<ID : AggregateId<*>> {

    /** Unique identifier of the entity */
    public abstract val id: ID

    /**
     * Checks equality based on identifier.
     *
     * Two entities are equal if they have the same identifier and are of the same type.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Entity<*>
        return id == other.id
    }

    /**
     * Returns hash code based on identifier.
     */
    override fun hashCode(): Int = id.hashCode()
}
