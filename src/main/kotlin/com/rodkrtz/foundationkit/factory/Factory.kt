package com.rodkrtz.foundationkit.factory

import com.rodkrtz.foundationkit.aggregate.AggregateRoot

/**
 * Base interface for domain factories.
 *
 * Factories encapsulate complex creation logic for aggregates. They are useful when:
 * - The creation process involves multiple steps
 * - Multiple objects need to be created together
 * - Complex validation is required during creation
 * - The creation depends on external services or data
 *
 * Example usage:
 * ```kotlin
 * // Simple factory
 * class UserFactory(
 *     private val emailService: EmailService,
 *     private val passwordEncoder: PasswordEncoder
 * ) : Factory<User> {
 *     
 *     override fun create(): User {
 *         val userId = UserId.generate()
 *         val verificationToken = emailService.generateToken()
 *         
 *         return User(
 *             id = userId,
 *             email = email,
 *             passwordHash = passwordEncoder.encode(password),
 *             verificationToken = verificationToken,
 *             status = UserStatus.PENDING_VERIFICATION
 *         )
 *     }
 * }
 *
 * // Factory with parameters (using primary constructor)
 * class OrderFactory(
 *     private val customerId: String,
 *     private val items: List<OrderItem>,
 *     private val shippingAddress: Address,
 *     private val pricingService: PricingService
 * ) : Factory<Order> {
 *     
 *     override fun create(): Order {
 *         val orderId = OrderId.generate()
 *         val subtotal = items.sumOf { it.price.amount }
 *         val tax = pricingService.calculateTax(subtotal)
 *         val shipping = pricingService.calculateShipping(shippingAddress)
 *         val total = subtotal + tax + shipping
 *         
 *         return Order(
 *             id = orderId,
 *             customerId = customerId,
 *             items = items,
 *             shippingAddress = shippingAddress,
 *             subtotal = Money.of(subtotal),
 *             tax = Money.of(tax),
 *             shipping = Money.of(shipping),
 *             total = Money.of(total),
 *             status = OrderStatus.PENDING
 *         )
 *     }
 * }
 *
 * // Usage
 * val userFactory = UserFactory(emailService, passwordEncoder)
 * val user = userFactory.create()
 *
 * val orderFactory = OrderFactory(
 *     customerId = "customer-123",
 *     items = listOf(
 *         OrderItem(productId = "prod-1", quantity = 2, price = Money.of(50.0))
 *     ),
 *     shippingAddress = address,
 *     pricingService = pricingService
 * )
 * val order = orderFactory.create()
 *
 * // Factory method pattern (alternative approach)
 * object UserFactory {
 *     fun createPendingUser(email: String, password: String): User {
 *         return User(
 *             id = UserId.generate(),
 *             email = email,
 *             passwordHash = hashPassword(password),
 *             status = UserStatus.PENDING
 *         )
 *     }
 *     
 *     fun createFromSocialLogin(provider: String, externalId: String): User {
 *         return User(
 *             id = UserId.generate(),
 *             socialProvider = provider,
 *             externalId = externalId,
 *             status = UserStatus.ACTIVE
 *         )
 *     }
 * }
 * ```
 *
 * @param T The type of aggregate root to create
 */
public interface Factory<T : AggregateRoot<*>> {
    /**
     * Creates and returns a new aggregate instance.
     *
     * @return A new, fully initialized aggregate root
     */
    public fun create(): T
}
