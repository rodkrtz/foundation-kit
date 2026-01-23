package com.rodkrtz.foundationkit.exception.application

/**
 * Exception thrown when a user attempts an action they are not authorized to perform.
 *
 * This is different from authentication (UnauthorizedException in presentation layer).
 * Authorization means the user is authenticated but lacks permission for a specific action.
 *
 * @property userId The identifier of the user attempting the action
 * @property resource The resource being accessed (e.g., "Order", "User")
 * @property action The action being attempted (e.g., "delete", "update", "view")
 *
 * Example:
 * ```kotlin
 * if (!user.canDelete(order)) {
 *     throw AuthorizationException(
 *         message = "User not authorized to delete this order",
 *         userId = user.id.value,
 *         resource = "Order",
 *         action = "delete"
 *     )
 * }
 * ```
 */
public class AuthorizationException(
    message: String,
    public val userId: String,
    public val resource: String,
    public val action: String
) : ApplicationException(message) {

    override fun toString(): String =
        "AuthorizationException(userId='$userId', resource='$resource', action='$action', message='$message')"
}