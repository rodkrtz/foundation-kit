package com.rodkrtz.foundationkit.repository

import com.rodkrtz.foundationkit.aggregate.AggregateRoot

/**
 * Unit of Work pattern for managing domain transactions.
 *
 * The Unit of Work pattern maintains a list of objects affected by a business
 * transaction and coordinates the writing out of changes. It helps to:
 * - Track all changes made during a business transaction
 * - Commit all changes atomically
 * - Rollback all changes if something fails
 * - Avoid unnecessary database calls
 *
 * Example usage:
 * ```kotlin
 * // Simple transaction
 * class TransferMoneyService(
 *     private val accountRepository: AccountRepository,
 *     private val unitOfWork: UnitOfWork
 * ) {
 *     fun transfer(fromId: AccountId, toId: AccountId, amount: Money) {
 *         unitOfWork.begin()
 *         
 *         try {
 *             val fromAccount = accountRepository.findById(fromId) 
 *                 ?: throw NotFoundException("Account", fromId.toString())
 *             val toAccount = accountRepository.findById(toId)
 *                 ?: throw NotFoundException("Account", toId.toString())
 *             
 *             fromAccount.debit(amount)
 *             toAccount.credit(amount)
 *             
 *             unitOfWork.registerDirty(fromAccount)
 *             unitOfWork.registerDirty(toAccount)
 *             
 *             unitOfWork.commit()
 *         } catch (e: Exception) {
 *             unitOfWork.rollback()
 *             throw e
 *         }
 *     }
 * }
 *
 * // Complex transaction with multiple operations
 * class OrderProcessingService(
 *     private val orderRepository: OrderRepository,
 *     private val inventoryRepository: InventoryRepository,
 *     private val customerRepository: CustomerRepository,
 *     private val unitOfWork: UnitOfWork
 * ) {
 *     fun processOrder(orderId: OrderId) {
 *         unitOfWork.begin()
 *         
 *         try {
 *             // Load aggregates
 *             val order = orderRepository.findById(orderId) 
 *                 ?: throw NotFoundException("Order", orderId.toString())
 *             val customer = customerRepository.findById(order.customerId)
 *                 ?: throw NotFoundException("Customer", order.customerId.toString())
 *             
 *             // Business logic
 *             order.items.forEach { item ->
 *                 val inventory = inventoryRepository.findByProductId(item.productId)
 *                     ?: throw NotFoundException("Inventory", item.productId.toString())
 *                 
 *                 inventory.reserve(item.quantity)
 *                 unitOfWork.registerDirty(inventory)
 *             }
 *             
 *             customer.addOrder(order)
 *             order.process()
 *             
 *             // Register changes
 *             unitOfWork.registerDirty(order)
 *             unitOfWork.registerDirty(customer)
 *             
 *             // Commit all changes atomically
 *             unitOfWork.commit()
 *             
 *             // Publish events after successful commit
 *             val events = order.getDomainEvents() + customer.getDomainEvents()
 *             eventPublisher.publish(events)
 *             
 *         } catch (e: Exception) {
 *             unitOfWork.rollback()
 *             throw e
 *         }
 *     }
 * }
 *
 * // With new aggregates
 * class CreateOrderService(
 *     private val orderRepository: OrderRepository,
 *     private val unitOfWork: UnitOfWork
 * ) {
 *     fun createOrder(customerId: String, items: List<OrderItem>): Order {
 *         unitOfWork.begin()
 *         
 *         try {
 *             val order = Order.create(customerId, items)
 *             
 *             unitOfWork.registerNew(order)
 *             unitOfWork.commit()
 *             
 *             return order
 *         } catch (e: Exception) {
 *             unitOfWork.rollback()
 *             throw e
 *         }
 *     }
 * }
 *
 * // Spring/Transaction integration
 * @Service
 * class OrderService(
 *     private val unitOfWork: UnitOfWork
 * ) {
 *     @Transactional // Spring manages the transaction
 *     fun processOrder(orderId: OrderId) {
 *         // UnitOfWork tracks changes
 *         // Spring commits/rollbacks based on @Transactional
 *         
 *         val order = orderRepository.findById(orderId)!!
 *         order.process()
 *         unitOfWork.registerDirty(order)
 *         
 *         // Commit happens automatically when method completes
 *     }
 * }
 * ```
 */
interface UnitOfWork {

    /**
     * Begins a new unit of work (transaction).
     *
     * Should be called before making any changes to aggregates.
     */
    fun begin()

    /**
     * Commits all pending changes atomically.
     *
     * Persists all registered new, dirty, and deleted aggregates.
     * If any operation fails, all changes should be rolled back.
     *
     * @throws Exception if commit fails
     */
    fun commit()

    /**
     * Rolls back all pending changes.
     *
     * Discards all registered changes without persisting them.
     */
    fun rollback()

    /**
     * Registers a new aggregate to be inserted.
     *
     * The aggregate will be persisted when commit() is called.
     *
     * @param aggregate The new aggregate to track
     */
    fun <T : AggregateRoot<*>> registerNew(aggregate: T)

    /**
     * Registers a modified aggregate to be updated.
     *
     * The aggregate's changes will be persisted when commit() is called.
     * New aggregates don't need to be registered as dirty.
     *
     * @param aggregate The modified aggregate to track
     */
    fun <T : AggregateRoot<*>> registerDirty(aggregate: T)

    /**
     * Registers an aggregate to be deleted.
     *
     * The aggregate will be removed when commit() is called.
     * If the aggregate was registered as new, it will simply be unregistered.
     *
     * @param aggregate The aggregate to delete
     */
    fun <T : AggregateRoot<*>> registerDeleted(aggregate: T)
}

/**
 * Abstract base implementation of Unit of Work.
 *
 * Provides default tracking of aggregates using in-memory collections.
 * Subclasses need to implement begin(), commit(), and rollback() with
 * actual persistence logic.
 */
abstract class AbstractUnitOfWork : UnitOfWork {

    /** Aggregates to be inserted */
    protected val newAggregates = mutableListOf<AggregateRoot<*>>()

    /** Aggregates to be updated */
    protected val dirtyAggregates = mutableListOf<AggregateRoot<*>>()

    /** Aggregates to be deleted */
    protected val deletedAggregates = mutableListOf<AggregateRoot<*>>()

    override fun <T : AggregateRoot<*>> registerNew(aggregate: T) {
        newAggregates.add(aggregate)
    }

    override fun <T : AggregateRoot<*>> registerDirty(aggregate: T) {
        if (aggregate !in newAggregates) {
            dirtyAggregates.add(aggregate)
        }
    }

    override fun <T : AggregateRoot<*>> registerDeleted(aggregate: T) {
        if (aggregate in newAggregates) {
            newAggregates.remove(aggregate)
        } else {
            dirtyAggregates.remove(aggregate)
            deletedAggregates.add(aggregate)
        }
    }

    /**
     * Clears all tracked aggregates.
     *
     * Should be called after successful commit or rollback.
     */
    protected fun clear() {
        newAggregates.clear()
        dirtyAggregates.clear()
        deletedAggregates.clear()
    }
}
