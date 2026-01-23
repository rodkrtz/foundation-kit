package com.rodkrtz.foundationkit.exception.application

/**
 * Exception thrown when a command fails validation at the application layer.
 *
 * This is different from domain validation (ValidationException). Command validation
 * checks if the command itself is well-formed and has all required data, while domain
 * validation checks business rules.
 *
 * @property commandType The fully qualified name or simple name of the command type
 * @property errors Map of field names to their validation error messages
 *
 * Example:
 * ```kotlin
 * throw CommandValidationException(
 *     message = "Invalid command data",
 *     commandType = "CreateUserCommand",
 *     errors = mapOf(
 *         "email" to listOf("Required", "Must be valid email"),
 *         "age" to listOf("Must be positive")
 *     )
 * )
 * ```
 */
public class CommandValidationException(
    message: String,
    public val commandType: String,
    public val errors: Map<String, List<String>> = emptyMap()
) : ApplicationException(message) {

    override fun toString(): String {
        val errorsString = errors.entries.joinToString(", ") { (field, msgs) ->
            "$field: ${msgs.joinToString("; ")}"
        }
        return "CommandValidationException(commandType='$commandType', message='$message', errors=[$errorsString])"
    }
}