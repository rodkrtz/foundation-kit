package com.rodkrtz.foundationkit.exception.domain

/**
 * Exception thrown when data validation fails.
 *
 * Validation exceptions represent input data that doesn't meet requirements:
 * - Missing required fields
 * - Invalid format (email, phone, etc.)
 * - Out-of-range values
 * - Type mismatches
 *
 * This exception can hold multiple validation errors, making it useful for
 * validating entire objects and returning all errors at once.
 *
 * Usage examples:
 * ```
 * // Single error
 * throw ValidationException("Invalid email format")
 *
 * // Multiple errors with field names
 * throw ValidationException(
 *     "Validation failed",
 *     "email" to "Invalid format",
 *     "age" to "Must be between 18 and 120"
 * )
 *
 * // With error map
 * val errors = mapOf(
 *     "username" to listOf("Required", "Must be at least 3 characters"),
 *     "password" to listOf("Required", "Must contain special character")
 * )
 * throw ValidationException("Multiple validation errors", errors)
 * ```
 *
 * @param message The main error message
 * @param errors Map of field names to lists of error messages
 */
public open class ValidationException(
    message: String,
    public val errors: Map<String, List<String>> = emptyMap()
) : DomainException(message) {
    /**
     * Convenience constructor for single errors per field.
     *
     * @param message The main error message
     * @param errors Variable number of field-to-error pairs
     */
    public constructor(message: String, vararg errors: Pair<String, String>) : this(
        message,
        errors.groupBy({ it.first }, { it.second })
    )
}