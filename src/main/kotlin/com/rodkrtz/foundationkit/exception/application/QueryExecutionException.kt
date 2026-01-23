package com.rodkrtz.foundationkit.exception.application

/**
 * Exception thrown when a query fails to execute at the application layer.
 *
 * This typically wraps infrastructure or domain exceptions that occur during
 * query execution.
 *
 * @property queryType The fully qualified name or simple name of the query type
 *
 * Example:
 * ```kotlin
 * try {
 *     return repository.findById(id)
 * } catch (e: DatabaseException) {
 *     throw QueryExecutionException(
 *         message = "Failed to execute query",
 *         queryType = "FindOrderByIdQuery",
 *         cause = e
 *     )
 * }
 * ```
 */
public class QueryExecutionException(
    message: String,
    public val queryType: String,
    cause: Throwable? = null
) : ApplicationException(message, cause) {

    override fun toString(): String =
        "QueryExecutionException(queryType='$queryType', message='$message', cause=${cause?.javaClass?.simpleName})"
}