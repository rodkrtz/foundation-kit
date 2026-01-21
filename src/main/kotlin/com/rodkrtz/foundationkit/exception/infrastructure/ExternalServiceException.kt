package com.rodkrtz.foundationkit.exception.infrastructure

/**
 * Exception thrown when an external service call fails.
 *
 * This is used for HTTP APIs, gRPC services, message queues, and other external dependencies.
 *
 * @property serviceName The name of the external service (e.g., "PaymentGateway", "EmailService")
 * @property statusCode HTTP status code if applicable, null otherwise
 * @property responseBody Response body from the service if available
 *
 * Example:
 * ```kotlin
 * try {
 *     val response = httpClient.post("https://api.payment.com/charge") {
 *         setBody(paymentData)
 *     }
 *
 *     if (response.status.value !in 200..299) {
 *         throw ExternalServiceException(
 *             message = "Payment service returned error",
 *             serviceName = "PaymentGateway",
 *             statusCode = response.status.value,
 *             responseBody = response.bodyAsText()
 *         )
 *     }
 * } catch (e: IOException) {
 *     throw ExternalServiceException(
 *         message = "Payment service unavailable",
 *         serviceName = "PaymentGateway",
 *         cause = e
 *     )
 * }
 * ```
 */
class ExternalServiceException(
    message: String,
    val serviceName: String,
    val statusCode: Int? = null,
    val responseBody: String? = null,
    cause: Throwable? = null
) : InfrastructureException(message, cause) {

    override fun toString(): String {
        val statusStr = statusCode?.let { ", statusCode=$it" } ?: ""
        val bodyPreview = responseBody?.take(100)?.let { ", body='$it...'" } ?: ""
        return "ExternalServiceException(service='$serviceName'$statusStr$bodyPreview, message='$message')"
    }
}