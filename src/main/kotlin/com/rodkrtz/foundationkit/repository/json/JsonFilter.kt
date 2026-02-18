package com.rodkrtz.foundationkit.repository.json

/**
 * Typed filter for JSON repository searches.
 *
 * The goal is to reduce the need for raw SQL/JSON snippets and keep
 * query building constrained to a safe, explicit contract.
 *
 * @param field JSON field path using dot notation (e.g. "status" or "address.city")
 * @param operator Comparison operator to be applied
 * @param value Value used by the operator
 */
public data class JsonFilter(
    val field: String,
    val operator: JsonOperator,
    val value: String
)

/**
 * Supported operators for [JsonFilter].
 */
public enum class JsonOperator {
    EQ,
    NEQ,
    GT,
    GTE,
    LT,
    LTE,
    LIKE
}

