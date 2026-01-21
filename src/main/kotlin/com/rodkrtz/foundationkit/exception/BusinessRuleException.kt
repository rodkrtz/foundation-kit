package com.rodkrtz.foundationkit.exception

/**
 * Exception thrown when a business rule is violated.
 *
 * Business rule exceptions represent violations of domain invariants or
 * business policies:
 * - Cannot cancel a completed order
 * - Cannot transfer more than available balance
 * - Cannot book overlapping reservations
 * - Maximum discount exceeded
 *
 * Business rules are part of the ubiquitous language and should be
 * meaningful to domain experts, not technical constraints.
 *
 * Usage examples:
 * ```
 * // Simple rule violation
 * throw BusinessRuleException("Cannot cancel completed order")
 *
 * // With rule name for tracking/logging
 * throw BusinessRuleException(
 *     "Insufficient balance for transfer",
 *     ruleName = "MinimumBalanceRule"
 * )
 * ```
 *
 * @param message Description of the rule violation
 * @param ruleName Optional name of the violated rule (useful for logging/monitoring)
 */
class BusinessRuleException(
    message: String,
    val ruleName: String? = null
) : DomainException(message)
