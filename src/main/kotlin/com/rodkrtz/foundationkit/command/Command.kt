package com.rodkrtz.foundationkit.command

import java.time.Instant
import java.util.UUID

/**
 * Marker interface for commands in CQRS pattern.
 *
 * Commands represent intentions to change the state of the system.
 * They are named with imperative verbs (e.g., CreateUser, UpdateOrder).
 *
 * Each command should:
 * - Contain all data needed to execute the action
 * - Be validated before execution
 * - Be immutable (data classes recommended)
 *
 * Note: Default implementations of commandId and timestamp regenerate
 * values on each access. Implementations should override with stored values.
 *
 * Example usage:
 * ```kotlin
 * // Simple command
 * data class CreateUserCommand(
 *     override val commandId: String = UUID.randomUUID().toString(),
 *     override val timestamp: Instant = Instant.now(),
 *     val email: String,
 *     val password: String,
 *     val name: String
 * ) : Command
 *
 * // Command with business data
 * data class PlaceOrderCommand(
 *     override val commandId: String = UUID.randomUUID().toString(),
 *     override val timestamp: Instant = Instant.now(),
 *     val customerId: String,
 *     val items: List<OrderItemData>,
 *     val shippingAddress: AddressData,
 *     val paymentMethod: PaymentMethodData
 * ) : Command
 *
 * data class OrderItemData(
 *     val productId: String,
 *     val quantity: Int,
 *     val price: BigDecimal
 * )
 *
 * // Command with metadata
 * data class TransferMoneyCommand(
 *     override val commandId: String = UUID.randomUUID().toString(),
 *     override val timestamp: Instant = Instant.now(),
 *     val fromAccountId: String,
 *     val toAccountId: String,
 *     val amount: BigDecimal,
 *     val currency: String,
 *     val description: String?,
 *     val requestedBy: String
 * ) : Command
 *
 * // Usage with CommandBus
 * val command = CreateUserCommand(
 *     email = "john@example.com",
 *     password = "securePassword123",
 *     name = "John Doe"
 * )
 *
 * val user = commandBus.dispatch(command)
 *
 * // Usage with CommandHandler directly
 * class CreateUserCommandHandler(
 *     private val userRepository: UserRepository,
 *     private val passwordEncoder: PasswordEncoder
 * ) : CommandHandler<CreateUserCommand, User> {
 *     
 *     override fun handle(command: CreateUserCommand): User {
 *         // Validate
 *         require(command.email.contains("@")) { "Invalid email" }
 *         require(command.password.length >= 8) { "Password too short" }
 *         
 *         // Execute
 *         val user = User.create(
 *             email = command.email,
 *             passwordHash = passwordEncoder.encode(command.password),
 *             name = command.name
 *         )
 *         
 *         // Persist
 *         userRepository.save(user)
 *         
 *         // Publish events
 *         eventPublisher.publish(user.getDomainEvents())
 *         user.clearDomainEvents()
 *         
 *         return user
 *     }
 * }
 *
 * // Command validation
 * data class UpdateUserProfileCommand(
 *     override val commandId: String = UUID.randomUUID().toString(),
 *     override val timestamp: Instant = Instant.now(),
 *     val userId: String,
 *     val name: String?,
 *     val bio: String?,
 *     val avatarUrl: String?
 * ) : Command {
 *     
 *     init {
 *         require(userId.isNotBlank()) { "User ID is required" }
 *         name?.let { require(it.length <= 100) { "Name too long" } }
 *         bio?.let { require(it.length <= 500) { "Bio too long" } }
 *     }
 * }
 *
 * // Async command execution
 * @Async
 * fun handleAsync(command: SendEmailCommand) {
 *     emailService.send(command.to, command.subject, command.body)
 * }
 *
 * // Command with Result
 * fun handle(command: ProcessPaymentCommand): Result<Payment> {
 *     return try {
 *         val payment = paymentGateway.process(command)
 *         Result.success(payment)
 *     } catch (e: PaymentException) {
 *         Result.failure(BusinessRuleException("Payment failed: ${e.message}"))
 *     }
 * }
 * ```
 */
interface Command {
    /**
     * Unique identifier for this command instance.
     * WARNING: Default implementation generates a new UUID on each access.
     * Override with a stored value in implementations.
     */
    val commandId: String
        get() = UUID.randomUUID().toString()

    /**
     * Timestamp when the command was created.
     * WARNING: Default implementation generates current time on each access.
     * Override with a stored value in implementations.
     */
    val timestamp: Instant
        get() = Instant.now()
}
