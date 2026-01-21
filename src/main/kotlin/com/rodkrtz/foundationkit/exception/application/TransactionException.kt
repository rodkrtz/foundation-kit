package com.rodkrtz.foundationkit.exception.application

/**
 * Exception thrown when a transaction operation fails.
 *
 * This typically occurs during commit, rollback, or other transaction lifecycle operations.
 *
 * Example:
 * ```kotlin
 * try {
 *     unitOfWork.begin()
 *     repository.save(order)
 *     unitOfWork.commit()
 * } catch (e: Exception) {
 *     unitOfWork.rollback()
 *     throw TransactionException(
 *         message = "Failed to commit transaction",
 *         cause = e
 *     )
 * }
 * ```
 */
class TransactionException(
    message: String,
    cause: Throwable? = null
) : ApplicationException(message, cause)