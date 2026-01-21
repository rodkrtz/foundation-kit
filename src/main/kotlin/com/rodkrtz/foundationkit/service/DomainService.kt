package com.rodkrtz.foundationkit.service

/**
 * Marker interface for domain services.
 *
 * Domain services encapsulate business logic that:
 * - Doesn't naturally belong to any specific entity or value object
 * - Operates on multiple aggregates
 * - Represents important business operations in the domain
 *
 * Domain services are stateless and focus on coordinating domain objects
 * to accomplish specific tasks.
 *
 * Example usage:
 * ```kotlin
 * // Transfer money between accounts
 * class MoneyTransferService : DomainService {
 *     
 *     fun transfer(
 *         fromAccount: Account,
 *         toAccount: Account,
 *         amount: Money
 *     ): TransferResult {
 *         // Validate business rules
 *         require(fromAccount.id != toAccount.id) { 
 *             "Cannot transfer to the same account" 
 *         }
 *         require(amount.amount > BigDecimal.ZERO) { 
 *             "Amount must be positive" 
 *         }
 *         require(fromAccount.hasBalance(amount)) { 
 *             "Insufficient balance" 
 *         }
 *         
 *         // Execute transfer
 *         fromAccount.debit(amount)
 *         toAccount.credit(amount)
 *         
 *         return TransferResult(
 *             transactionId = UUID.randomUUID().toString(),
 *             amount = amount,
 *             timestamp = Instant.now()
 *         )
 *     }
 * }
 *
 * // Calculate shipping cost
 * class ShippingCostCalculator(
 *     private val distanceService: DistanceService,
 *     private val rateTable: ShippingRateTable
 * ) : DomainService {
 *     
 *     fun calculateShippingCost(
 *         origin: Address,
 *         destination: Address,
 *         weight: Weight,
 *         shippingMethod: ShippingMethod
 *     ): Money {
 *         val distance = distanceService.calculate(origin, destination)
 *         val baseRate = rateTable.getRate(shippingMethod, distance)
 *         val weightSurcharge = calculateWeightSurcharge(weight)
 *         
 *         return Money.of(baseRate + weightSurcharge, "BRL")
 *     }
 *     
 *     private fun calculateWeightSurcharge(weight: Weight): BigDecimal {
 *         return when {
 *             weight.kilograms > 10 -> weight.kilograms * BigDecimal("2.50")
 *             weight.kilograms > 5 -> weight.kilograms * BigDecimal("1.50")
 *             else -> BigDecimal.ZERO
 *         }
 *     }
 * }
 *
 * // Validate order placement
 * class OrderValidationService(
 *     private val inventoryService: InventoryService,
 *     private val customerRepository: CustomerRepository
 * ) : DomainService {
 *     
 *     fun validateOrderPlacement(order: Order): ValidationResult {
 *         val errors = mutableListOf<String>()
 *         
 *         // Check customer status
 *         val customer = customerRepository.findById(order.customerId)
 *         if (customer == null) {
 *             errors.add("Customer not found")
 *         } else if (!customer.isActive) {
 *             errors.add("Customer account is inactive")
 *         } else if (customer.hasOutstandingDebt()) {
 *             errors.add("Customer has outstanding debt")
 *         }
 *         
 *         // Check inventory
 *         order.items.forEach { item ->
 *             val available = inventoryService.getAvailableQuantity(item.productId)
 *             if (available < item.quantity) {
 *                 errors.add("Insufficient stock for product ${item.productId}")
 *             }
 *         }
 *         
 *         return if (errors.isEmpty()) {
 *             ValidationResult.Valid
 *         } else {
 *             ValidationResult.Invalid(errors)
 *         }
 *     }
 * }
 *
 * sealed class ValidationResult {
 *     object Valid : ValidationResult()
 *     data class Invalid(val errors: List<String>) : ValidationResult()
 * }
 *
 * // Calculate pricing with discounts
 * class PricingService(
 *     private val discountRules: List<DiscountRule>
 * ) : DomainService {
 *     
 *     fun calculatePrice(
 *         items: List<OrderItem>,
 *         customer: Customer
 *     ): PriceBreakdown {
 *         val subtotal = items.sumOf { it.price.amount * it.quantity.toBigDecimal() }
 *         
 *         val applicableDiscounts = discountRules
 *             .filter { it.appliesTo(customer, items) }
 *             .map { it.calculate(subtotal) }
 *         
 *         val totalDiscount = applicableDiscounts.sumOf { it.amount }
 *         val tax = (subtotal - totalDiscount) * BigDecimal("0.15") // 15% tax
 *         val total = subtotal - totalDiscount + tax
 *         
 *         return PriceBreakdown(
 *             subtotal = Money.of(subtotal),
 *             discounts = applicableDiscounts,
 *             tax = Money.of(tax),
 *             total = Money.of(total)
 *         )
 *     }
 * }
 *
 * interface DiscountRule {
 *     fun appliesTo(customer: Customer, items: List<OrderItem>): Boolean
 *     fun calculate(subtotal: BigDecimal): Money
 * }
 *
 * // Coordinate saga/process
 * class OrderFulfillmentService(
 *     private val inventoryService: InventoryService,
 *     private val paymentService: PaymentService,
 *     private val shippingService: ShippingService
 * ) : DomainService {
 *     
 *     fun fulfillOrder(order: Order): FulfillmentResult {
 *         // 1. Reserve inventory
 *         val reservation = inventoryService.reserve(order.items)
 *         if (!reservation.successful) {
 *             return FulfillmentResult.Failed("Failed to reserve inventory")
 *         }
 *         
 *         try {
 *             // 2. Process payment
 *             val payment = paymentService.charge(order.total, order.paymentMethod)
 *             if (!payment.successful) {
 *                 inventoryService.releaseReservation(reservation.id)
 *                 return FulfillmentResult.Failed("Payment failed")
 *             }
 *             
 *             // 3. Create shipment
 *             val shipment = shippingService.createShipment(order)
 *             
 *             return FulfillmentResult.Success(
 *                 reservationId = reservation.id,
 *                 paymentId = payment.id,
 *                 shipmentId = shipment.id
 *             )
 *             
 *         } catch (e: Exception) {
 *             // Rollback on error
 *             inventoryService.releaseReservation(reservation.id)
 *             throw e
 *         }
 *     }
 * }
 *
 * sealed class FulfillmentResult {
 *     data class Success(
 *         val reservationId: String,
 *         val paymentId: String,
 *         val shipmentId: String
 *     ) : FulfillmentResult()
 *     
 *     data class Failed(val reason: String) : FulfillmentResult()
 * }
 * ```
 */
interface DomainService
