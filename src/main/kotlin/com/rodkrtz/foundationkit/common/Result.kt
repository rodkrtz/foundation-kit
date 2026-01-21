package com.rodkrtz.foundationkit.common

import com.rodkrtz.foundationkit.exception.domain.DomainException

/**
 * Result wrapper for operations that can succeed or fail.
 *
 * This sealed class represents the outcome of an operation in a functional way,
 * avoiding exceptions for expected failures. It's inspired by functional programming
 * patterns like Either and Result types.
 *
 * Benefits:
 * - Makes failures explicit in the type system
 * - Enables composition of operations via map/flatMap
 * - Cleaner error handling without try-catch blocks
 * - Railway-oriented programming patterns
 *
 * Example usage:
 * ```kotlin
 * // Simple success/failure
 * fun createUser(email: String, password: String): Result<User> {
 *     return if (isValidEmail(email)) {
 *         if (password.length >= 8) {
 *             Result.success(User(email, password))
 *         } else {
 *             Result.failure(ValidationException("Password must be at least 8 characters"))
 *         }
 *     } else {
 *         Result.failure(ValidationException("Invalid email format"))
 *     }
 * }
 *
 * // Pattern matching
 * val result = createUser("test@example.com", "password123")
 * when (result) {
 *     is Result.Success -> println("User created: ${result.value.email}")
 *     is Result.Failure -> println("Error: ${result.error.message}")
 * }
 *
 * // Chaining operations with map
 * val emailResult = createUser("test@example.com", "password123")
 *     .map { user -> user.email }
 *     .map { email -> email.uppercase() }
 *
 * // Chaining operations with flatMap
 * fun findUser(email: String): Result<User> { /* ... */ }
 * fun activateUser(user: User): Result<User> { /* ... */ }
 * fun sendWelcomeEmail(user: User): Result<Unit> { /* ... */ }
 *
 * val result = findUser("test@example.com")
 *     .flatMap { user -> activateUser(user) }
 *     .flatMap { user -> sendWelcomeEmail(user) }
 *
 * // Railway-oriented programming
 * val processOrder = findOrder(orderId)
 *     .flatMap { validateStock(it) }
 *     .flatMap { reserveItems(it) }
 *     .flatMap { processPayment(it) }
 *     .flatMap { confirmOrder(it) }
 *     .map { order -> OrderConfirmation(order.id) }
 *
 * when (processOrder) {
 *     is Success -> sendConfirmationEmail(processOrder.value)
 *     is Failure -> handleOrderError(processOrder.error)
 * }
 *
 * // Safe value extraction
 * val user: User? = result.getOrNull()
 * val userOrThrow: User = result.getOrThrow() // throws if Failure
 *
 * // Checking result type
 * if (result.isSuccess()) {
 *     // handle success
 * } else {
 *     // handle failure
 * }
 *
 * // With multiple errors
 * Result.failure(
 *     ValidationException("Multiple validation errors"),
 *     errors = listOf(
 *         "Email is required",
 *         "Password must be at least 8 characters",
 *         "Username already taken"
 *     )
 * )
 * ```
 *
 * @param T The type of value on success
 */
sealed class Result<out T> {

    /**
     * Represents a successful operation with a value.
     *
     * @property value The successful result value
     */
    data class Success<T>(val value: T) : Result<T>()

    /**
     * Represents a failed operation with an error.
     *
     * @property error The domain exception that caused the failure
     * @property errors Optional list of additional error messages
     */
    data class Failure(
        val error: DomainException,
        val errors: List<String> = emptyList()
    ) : Result<Nothing>()

    /**
     * Checks if this result is a success.
     *
     * @return true if Success, false if Failure
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Checks if this result is a failure.
     *
     * @return true if Failure, false if Success
     */
    fun isFailure(): Boolean = this is Failure

    /**
     * Returns the value if Success, or null if Failure.
     *
     * @return The value or null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }

    /**
     * Returns the value if Success, or throws the error if Failure.
     *
     * @return The value
     * @throws DomainException if this is a Failure
     */
    fun getOrThrow(): T = when (this) {
        is Success -> value
        is Failure -> throw error
    }

    /**
     * Transforms the success value using the given function.
     *
     * If this is a Failure, returns the failure unchanged.
     *
     * @param transform Function to transform the success value
     * @return Result with transformed value or original failure
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    /**
     * Transforms the success value using a function that returns another Result.
     *
     * Useful for chaining operations that can fail. If this is a Failure,
     * returns the failure unchanged without calling the transform function.
     *
     * @param transform Function that transforms value to another Result
     * @return Result from the transform or original failure
     */
    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    companion object {
        /**
         * Creates a successful Result with the given value.
         *
         * @param value The success value
         * @return Success result
         */
        fun <T> success(value: T): Result<T> = Success(value)

        /**
         * Creates a failed Result with the given error.
         *
         * @param error The domain exception
         * @param errors Optional list of additional error messages
         * @return Failure result
         */
        fun failure(error: DomainException, errors: List<String> = emptyList()): Result<Nothing> =
            Failure(error, errors)
    }
}
