package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Exception thrown when a cache operation fails.
 *
 * @property key The cache key that was being accessed
 * @property operation The cache operation (e.g., "get", "put", "delete", "clear")
 *
 * Example:
 * ```kotlin
 * try {
 *     cache.put(key, value)
 * } catch (e: Exception) {
 *     throw CacheException(
 *         message = "Failed to store value in cache",
 *         key = key,
 *         operation = "put",
 *         cause = e
 *     )
 * }
 * ```
 */
public class CacheException(
    message: String,
    public val key: String,
    public val operation: String,
    cause: Throwable? = null
) : InfrastructureException(message, cause)