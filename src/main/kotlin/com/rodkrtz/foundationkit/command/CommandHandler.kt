package com.rodkrtz.foundationkit.command

/**
 * Handler for processing commands in CQRS pattern.
 *
 * Command handlers contain the business logic for executing commands.
 * Each handler is responsible for:
 * - Validating the command
 * - Loading necessary aggregates
 * - Executing the business operation
 * - Persisting changes
 * - Publishing domain events
 *
 * Handlers should be stateless and focused on a single command type.
 *
 * Example usage:
 * ```kotlin
 * // Simple command handler
 * class CreateUserCommandHandler(
 *     private val userRepository: UserRepository,
 *     private val passwordEncoder: PasswordEncoder,
 *     private val eventPublisher: EventPublisher
 * ) : CommandHandler<CreateUserCommand, User> {
 *     
 *     override fun handle(command: CreateUserCommand): User {
 *         // 1. Validate
 *         require(command.email.contains("@")) { "Invalid email format" }
 *         require(command.password.length >= 8) { "Password must be at least 8 characters" }
 *         
 *         // 2. Check business rules
 *         val existing = userRepository.findByEmail(command.email)
 *         if (existing != null) {
 *             throw BusinessRuleException("Email already registered")
 *         }
 *         
 *         // 3. Create aggregate
 *         val user = User.create(
 *             id = UserId.generate(),
 *             email = Email(command.email),
 *             passwordHash = passwordEncoder.encode(command.password),
 *             name = command.name
 *         )
 *         
 *         // 4. Persist
 *         userRepository.save(user)
 *         
 *         // 5. Publish events
 *         eventPublisher.publish(user.getDomainEvents())
 *         user.clearDomainEvents()
 *         
 *         return user
 *     }
 * }
 *
 * // Command handler with multiple aggregates
 * class PlaceOrderCommandHandler(
 *     private val orderRepository: OrderRepository,
 *     private val customerRepository: CustomerRepository,
 *     private val inventoryRepository: InventoryRepository,
 *     private val pricingService: PricingService,
 *     private val unitOfWork: UnitOfWork,
 *     private val eventPublisher: EventPublisher
 * ) : CommandHandler<PlaceOrderCommand, Order> {
 *     
 *     override fun handle(command: PlaceOrderCommand): Order {
 *         unitOfWork.begin()
 *         
 *         try {
 *             // 1. Load customer
 *             val customer = customerRepository.findById(CustomerId(command.customerId))
 *                 ?: throw NotFoundException("Customer", command.customerId)
 *             
 *             // 2. Validate customer can place order
 *             if (!customer.canPlaceOrder()) {
 *                 throw BusinessRuleException("Customer cannot place orders")
 *             }
 *             
 *             // 3. Check inventory
 *             command.items.forEach { item ->
 *                 val inventory = inventoryRepository.findByProductId(item.productId)
 *                     ?: throw NotFoundException("Product", item.productId)
 *                 
 *                 if (!inventory.hasStock(item.quantity)) {
 *                     throw BusinessRuleException("Insufficient stock for ${item.productId}")
 *                 }
 *             }
 *             
 *             // 4. Calculate price
 *             val pricing = pricingService.calculatePrice(command.items, customer)
 *             
 *             // 5. Create order
 *             val order = Order.create(
 *                 customerId = customer.id,
 *                 items = command.items.map { OrderItem.from(it) },
 *                 shippingAddress = Address.from(command.shippingAddress),
 *                 pricing = pricing
 *             )
 *             
 *             // 6. Reserve inventory
 *             command.items.forEach { item ->
 *                 val inventory = inventoryRepository.findByProductId(item.productId)!!
 *                 inventory.reserve(item.quantity, order.id)
 *                 unitOfWork.registerDirty(inventory)
 *             }
 *             
 *             // 7. Update customer
 *             customer.recordOrder(order.id)
 *             unitOfWork.registerDirty(customer)
 *             
 *             // 8. Save order
 *             unitOfWork.registerNew(order)
 *             
 *             // 9. Commit transaction
 *             unitOfWork.commit()
 *             
 *             // 10. Publish events
 *             val events = order.getDomainEvents() + 
 *                         customer.getDomainEvents() +
 *                         inventoryRepository.findAll()
 *                             .flatMap { it.getDomainEvents() }
 *             eventPublisher.publish(events)
 *             
 *             return order
 *             
 *         } catch (e: Exception) {
 *             unitOfWork.rollback()
 *             throw e
 *         }
 *     }
 * }
 *
 * // Command handler with Result return type
 * class TransferMoneyCommandHandler(
 *     private val accountRepository: AccountRepository,
 *     private val unitOfWork: UnitOfWork
 * ) : CommandHandler<TransferMoneyCommand, Result<Transfer>> {
 *     
 *     override fun handle(command: TransferMoneyCommand): Result<Transfer> {
 *         return try {
 *             unitOfWork.begin()
 *             
 *             val fromAccount = accountRepository.findById(AccountId(command.fromAccountId))
 *                 ?: return Result.failure(NotFoundException("Account", command.fromAccountId))
 *             
 *             val toAccount = accountRepository.findById(AccountId(command.toAccountId))
 *                 ?: return Result.failure(NotFoundException("Account", command.toAccountId))
 *             
 *             val amount = Money.of(command.amount, command.currency)
 *             
 *             if (!fromAccount.hasBalance(amount)) {
 *                 return Result.failure(BusinessRuleException("Insufficient balance"))
 *             }
 *             
 *             fromAccount.debit(amount, command.description)
 *             toAccount.credit(amount, command.description)
 *             
 *             unitOfWork.registerDirty(fromAccount)
 *             unitOfWork.registerDirty(toAccount)
 *             unitOfWork.commit()
 *             
 *             val transfer = Transfer(
 *                 id = TransferId.generate(),
 *                 fromAccountId = fromAccount.id,
 *                 toAccountId = toAccount.id,
 *                 amount = amount,
 *                 timestamp = Instant.now()
 *             )
 *             
 *             Result.success(transfer)
 *             
 *         } catch (e: DomainException) {
 *             unitOfWork.rollback()
 *             Result.failure(e)
 *         } catch (e: Exception) {
 *             unitOfWork.rollback()
 *             throw e
 *         }
 *     }
 * }
 *
 * // Async command handler
 * class SendEmailCommandHandler(
 *     private val emailService: EmailService
 * ) : CommandHandler<SendEmailCommand, Unit> {
 *     
 *     @Async
 *     override fun handle(command: SendEmailCommand) {
 *         emailService.send(
 *             to = command.to,
 *             subject = command.subject,
 *             body = command.body,
 *             attachments = command.attachments
 *         )
 *     }
 * }
 *
 * // Command handler with validation
 * class UpdateUserProfileCommandHandler(
 *     private val userRepository: UserRepository,
 *     private val validator: UserProfileValidator
 * ) : CommandHandler<UpdateUserProfileCommand, User> {
 *     
 *     override fun handle(command: UpdateUserProfileCommand): User {
 *         // Validate command
 *         val validationResult = validator.validate(command)
 *         if (validationResult.hasErrors()) {
 *             throw ValidationException(
 *                 "Invalid user profile data",
 *                 validationResult.errors
 *             )
 *         }
 *         
 *         // Load aggregate
 *         val user = userRepository.findById(UserId(command.userId))
 *             ?: throw NotFoundException("User", command.userId)
 *         
 *         // Apply changes
 *         command.name?.let { user.updateName(it) }
 *         command.bio?.let { user.updateBio(it) }
 *         command.avatarUrl?.let { user.updateAvatar(it) }
 *         
 *         // Save
 *         return userRepository.save(user)
 *     }
 * }
 *
 * // Command handler with authorization
 * class DeleteOrderCommandHandler(
 *     private val orderRepository: OrderRepository,
 *     private val authorizationService: AuthorizationService
 * ) : CommandHandler<DeleteOrderCommand, Unit> {
 *     
 *     override fun handle(command: DeleteOrderCommand) {
 *         val order = orderRepository.findById(OrderId(command.orderId))
 *             ?: throw NotFoundException("Order", command.orderId)
 *         
 *         // Check authorization
 *         if (!authorizationService.canDelete(command.requestedBy, order)) {
 *             throw AuthorizationException("User cannot delete this order")
 *         }
 *         
 *         // Check business rules
 *         if (!order.canBeDeleted()) {
 *             throw BusinessRuleException("Order cannot be deleted in current state")
 *         }
 *         
 *         orderRepository.delete(order.id)
 *     }
 * }
 *
 * // Usage with CommandBus
 * val command = CreateUserCommand(
 *     email = "john@example.com",
 *     password = "securePass123",
 *     name = "John Doe"
 * )
 * 
 * val user = commandBus.dispatch(command)
 * println("User created with ID: ${user.id}")
 *
 * // Direct usage
 * val handler = CreateUserCommandHandler(userRepo, encoder, publisher)
 * val user = handler.handle(command)
 * ```
 *
 * @param C The type of command this handler processes
 * @param R The type of result returned after processing
 */
interface CommandHandler<C : Command, R> {
    /**
     * Handles the given command and returns a result.
     *
     * @param command The command to process
     * @return The result of command execution
     */
    fun handle(command: C): R
}
