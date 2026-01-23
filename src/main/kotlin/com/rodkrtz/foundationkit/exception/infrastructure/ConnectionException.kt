package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Exception thrown when a network connection fails.
 *
 * @property host The hostname or IP address that couldn't be reached
 * @property port The port number that was attempted
 *
 * Example:
 * ```kotlin
 * try {
 *     socket.connect(InetSocketAddress(host, port), timeout)
 * } catch (e: IOException) {
 *     throw ConnectionException(
 *         message = "Failed to connect to server",
 *         host = "api.example.com",
 *         port = 443,
 *         cause = e
 *     )
 * }
 * ```
 */
public class ConnectionException(
    message: String,
    public val host: String,
    public val port: Int,
    cause: Throwable? = null
) : InfrastructureException(message, cause) {

    override fun toString(): String =
        "ConnectionException(host='$host', port=$port, message='$message', cause=${cause?.javaClass?.simpleName})"
}