package com.rodkrtz.foundationkit.exception

/**
 * Root exception for all Foundation Kit exceptions.
 *
 * This is the base exception for the entire Foundation Kit library hierarchy.
 * All other exceptions in the library inherit from this class, enabling users
 * to catch all Foundation Kit exceptions with a single catch block if needed.
 *
 * Benefits:
 * - Single catch point for all library exceptions
 * - Clear separation from other libraries and frameworks
 * - Easier debugging (all Foundation Kit exceptions are identifiable)
 * - Better exception handling strategies
 * - Professional naming following industry standards (HibernateException, JacksonException)
 *
 * @property message Error message describing what went wrong
 * @property cause Original exception that caused this error, if any
 *
 * Example - Catch all Foundation Kit exceptions:
 * ```kotlin
 * try {
 *     orderService.placeOrder(command)
 * } catch (e: FoundationKitException) {
 *     // Catch all Foundation Kit exceptions
 *     logger.error("Foundation Kit error: ${e.message}", e)
 * }
 * ```
 *
 * Example - Pattern matching by layer:
 * ```kotlin
 * try {
 *     orderService.placeOrder(command)
 * } catch (e: FoundationKitException) {
 *     when (e) {
 *         is DomainException -> {
 *             // Handle business rule violations
 *             logger.warn("Domain error: ${e.message}")
 *             return ResponseEntity.badRequest()
 *         }
 *         is ApplicationException -> {
 *             // Handle application errors
 *             logger.error("Application error: ${e.message}")
 *             return ResponseEntity.internalServerError()
 *         }
 *         is InfrastructureException -> {
 *             // Handle technical errors
 *             logger.error("Infrastructure error: ${e.message}", e)
 *             return ResponseEntity.status(503) // Service Unavailable
 *         }
 *     }
 * }
 * ```
 *
 * Example - Separate Foundation Kit from other libraries:
 * ```kotlin
 * try {
 *     processOrder()
 * } catch (e: FoundationKitException) {
 *     // Errors from Foundation Kit
 *     handleFoundationKitError(e)
 * } catch (e: DataAccessException) {
 *     // Errors from Spring
 *     handleSpringError(e)
 * } catch (e: Exception) {
 *     // Other errors
 *     handleGenericError(e)
 * }
 * ```
 *
 * @since 1.0.0
 * @see com.rodkrtz.foundationkit.exception.domain.DomainException
 * @see com.rodkrtz.foundationkit.exception.application.ApplicationException
 * @see com.rodkrtz.foundationkit.exception.infrastructure.InfrastructureException
 */
abstract class FoundationKitException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)