package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Exception thrown when a file system operation fails.
 *
 * @property path The file or directory path where the error occurred
 * @property operation The operation that failed (e.g., "read", "write", "delete", "create")
 *
 * Example:
 * ```kotlin
 * try {
 *     File(path).writeText(content)
 * } catch (e: IOException) {
 *     throw FileSystemException(
 *         message = "Failed to write file",
 *         path = path,
 *         operation = "write",
 *         cause = e
 *     )
 * }
 * ```
 */
public class FileSystemException(
    message: String,
    public val path: String,
    public val operation: String,
    cause: Throwable? = null
) : InfrastructureException(message, cause) {

    override fun toString(): String =
        "FileSystemException(path='$path', operation='$operation', message='$message')"
}