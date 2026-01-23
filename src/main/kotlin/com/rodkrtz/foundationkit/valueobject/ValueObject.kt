package com.rodkrtz.foundationkit.valueobject

/**
 * Marker interface for value objects.
 *
 * Value objects are immutable objects that are defined by their attributes
 * rather than by a unique identity. Two value objects with the same attributes
 * are considered equal.
 *
 * Characteristics of value objects:
 * - Immutable (cannot be changed after creation)
 * - Equality based on values, not identity
 * - No unique identifier
 * - Self-validating (validation in constructor/init block)
 *
 * Use Kotlin data classes to implement value objects for automatic
 * equals/hashCode/copy implementations.
 *
 * Examples: Money, Email, DateRange, Coordinates, Address
 */
public interface ValueObject
