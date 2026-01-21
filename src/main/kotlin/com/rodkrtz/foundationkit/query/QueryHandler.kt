package com.rodkrtz.foundationkit.query

/**
 * Handler for processing queries in CQRS pattern.
 *
 * Query handlers contain the logic for retrieving data without modifying state.
 * Each handler is responsible for:
 * - Validating the query parameters
 * - Fetching data from read models or projections
 * - Transforming data to the requested format
 * - Returning the result
 *
 * Query handlers should be optimized for read performance and can bypass
 * the domain model to query directly from read-optimized data stores.
 *
 * Example usage:
 * ```kotlin
 * // Simple query handler
 * class FindUserByIdQueryHandler(
 *     private val userRepository: UserRepository
 * ) : QueryHandler<FindUserByIdQuery, User?> {
 *     
 *     override fun handle(query: FindUserByIdQuery): User? {
 *         return userRepository.findById(UserId(query.userId))
 *     }
 * }
 *
 * // Query handler with DTO projection
 * class GetUserProfileQueryHandler(
 *     private val jdbcTemplate: JdbcTemplate
 * ) : QueryHandler<GetUserProfileQuery, UserProfileDTO> {
 *     
 *     override fun handle(query: GetUserProfileQuery): UserProfileDTO {
 *         val sql = """
 *             SELECT 
 *                 u.id,
 *                 u.name,
 *                 u.email,
 *                 u.avatar_url,
 *                 u.created_at,
 *                 COUNT(o.id) as order_count,
 *                 SUM(o.total_amount) as total_spent
 *             FROM users u
 *             LEFT JOIN orders o ON o.customer_id = u.id
 *             WHERE u.id = ?
 *             GROUP BY u.id
 *         """
 *         
 *         return jdbcTemplate.queryForObject(sql, query.userId) { rs, _ ->
 *             UserProfileDTO(
 *                 id = rs.getString("id"),
 *                 name = rs.getString("name"),
 *                 email = rs.getString("email"),
 *                 avatarUrl = rs.getString("avatar_url"),
 *                 memberSince = rs.getDate("created_at").toLocalDate(),
 *                 orderCount = rs.getInt("order_count"),
 *                 totalSpent = Money.of(rs.getBigDecimal("total_spent"), "BRL")
 *             )
 *         }
 *     }
 * }
 *
 * // Paginated query handler
 * class ListProductsQueryHandler(
 *     private val jdbcTemplate: JdbcTemplate
 * ) : QueryHandler<ListProductsQuery, Page<ProductDTO>> {
 *     
 *     override fun handle(query: ListProductsQuery): Page<ProductDTO> {
 *         // Build WHERE clause
 *         val conditions = mutableListOf<String>()
 *         val params = mutableListOf<Any>()
 *         
 *         query.category?.let {
 *             conditions.add("p.category = ?")
 *             params.add(it)
 *         }
 *         
 *         query.minPrice?.let {
 *             conditions.add("p.price >= ?")
 *             params.add(it)
 *         }
 *         
 *         query.maxPrice?.let {
 *             conditions.add("p.price <= ?")
 *             params.add(it)
 *         }
 *         
 *         val whereClause = if (conditions.isNotEmpty()) {
 *             "WHERE " + conditions.joinToString(" AND ")
 *         } else {
 *             ""
 *         }
 *         
 *         // Count total
 *         val countSql = "SELECT COUNT(*) FROM products p $whereClause"
 *         val total = jdbcTemplate.queryForObject(countSql, Long::class.java, *params.toTypedArray())!!
 *         
 *         // Fetch page
 *         val dataSql = """
 *             SELECT p.id, p.name, p.description, p.price, p.category, p.stock
 *             FROM products p
 *             $whereClause
 *             ORDER BY p.name
 *             LIMIT ? OFFSET ?
 *         """
 *         
 *         params.add(query.pageSize)
 *         params.add(query.pageNumber * query.pageSize)
 *         
 *         val products = jdbcTemplate.query(dataSql, params.toTypedArray()) { rs, _ ->
 *             ProductDTO(
 *                 id = rs.getString("id"),
 *                 name = rs.getString("name"),
 *                 description = rs.getString("description"),
 *                 price = Money.of(rs.getBigDecimal("price"), "BRL"),
 *                 category = rs.getString("category"),
 *                 stock = rs.getInt("stock")
 *             )
 *         }
 *         
 *         return Page(
 *             content = products,
 *             pageNumber = query.pageNumber,
 *             pageSize = query.pageSize,
 *             totalElements = total
 *         )
 *     }
 * }
 *
 * // Query handler with complex aggregation
 * class GetOrderSummaryQueryHandler(
 *     private val jdbcTemplate: JdbcTemplate
 * ) : QueryHandler<GetOrderSummaryQuery, OrderSummaryDTO> {
 *     
 *     override fun handle(query: GetOrderSummaryQuery): OrderSummaryDTO {
 *         val order = findOrder(query.orderId)
 *         val items = findOrderItems(query.orderId)
 *         val customer = findCustomer(order.customerId)
 *         val shippingStatus = findShippingStatus(query.orderId)
 *         
 *         return OrderSummaryDTO(
 *             orderId = order.id,
 *             orderNumber = order.number,
 *             status = order.status,
 *             customer = CustomerInfo(
 *                 id = customer.id,
 *                 name = customer.name,
 *                 email = customer.email
 *             ),
 *             items = items.map { item ->
 *                 OrderItemInfo(
 *                     productName = item.productName,
 *                     quantity = item.quantity,
 *                     unitPrice = item.unitPrice,
 *                     subtotal = item.subtotal
 *                 )
 *             },
 *             subtotal = order.subtotal,
 *             tax = order.tax,
 *             shipping = order.shipping,
 *             total = order.total,
 *             shippingAddress = order.shippingAddress,
 *             shippingStatus = shippingStatus,
 *             createdAt = order.createdAt,
 *             updatedAt = order.updatedAt
 *         )
 *     }
 *     
 *     private fun findOrder(orderId: String): OrderData { /* ... */ }
 *     private fun findOrderItems(orderId: String): List<OrderItemData> { /* ... */ }
 *     private fun findCustomer(customerId: String): CustomerData { /* ... */ }
 *     private fun findShippingStatus(orderId: String): ShippingStatus { /* ... */ }
 * }
 *
 * // Query handler with caching
 * @Cacheable("user-profiles", key = "#query.userId")
 * class CachedGetUserProfileQueryHandler(
 *     private val delegate: GetUserProfileQueryHandler
 * ) : QueryHandler<GetUserProfileQuery, UserProfileDTO> {
 *     
 *     override fun handle(query: GetUserProfileQuery): UserProfileDTO {
 *         return delegate.handle(query)
 *     }
 * }
 *
 * // Query handler with Result
 * class FindOrderByIdQueryHandler(
 *     private val orderRepository: OrderRepository
 * ) : QueryHandler<FindOrderByIdQuery, Result<Order>> {
 *     
 *     override fun handle(query: FindOrderByIdQuery): Result<Order> {
 *         val order = orderRepository.findById(OrderId(query.orderId))
 *         
 *         return if (order != null) {
 *             Result.success(order)
 *         } else {
 *             Result.failure(NotFoundException("Order", query.orderId))
 *         }
 *     }
 * }
 *
 * // Query handler with authorization
 * class GetOrderDetailsQueryHandler(
 *     private val orderRepository: OrderRepository,
 *     private val authorizationService: AuthorizationService
 * ) : QueryHandler<GetOrderDetailsQuery, OrderDetails> {
 *     
 *     override fun handle(query: GetOrderDetailsQuery): OrderDetails {
 *         val order = orderRepository.findById(OrderId(query.orderId))
 *             ?: throw NotFoundException("Order", query.orderId)
 *         
 *         // Check if user can view this order
 *         if (!authorizationService.canView(query.requestedBy, order)) {
 *             throw AuthorizationException("User cannot view this order")
 *         }
 *         
 *         return OrderDetails.from(order)
 *     }
 * }
 *
 * // Query handler with filtering and sorting
 * class SearchUsersQueryHandler(
 *     private val jdbcTemplate: JdbcTemplate
 * ) : QueryHandler<SearchUsersQuery, List<UserDTO>> {
 *     
 *     override fun handle(query: SearchUsersQuery): List<UserDTO> {
 *         val sql = """
 *             SELECT id, name, email, created_at
 *             FROM users
 *             WHERE 
 *                 (name ILIKE ? OR email ILIKE ?)
 *                 AND deleted = false
 *             ORDER BY ${getSortColumn(query.sortBy)} ${query.sortDirection}
 *             LIMIT 100
 *         """
 *         
 *         val searchPattern = "%${query.searchTerm}%"
 *         
 *         return jdbcTemplate.query(sql, searchPattern, searchPattern) { rs, _ ->
 *             UserDTO(
 *                 id = rs.getString("id"),
 *                 name = rs.getString("name"),
 *                 email = rs.getString("email"),
 *                 createdAt = rs.getTimestamp("created_at").toInstant()
 *             )
 *         }
 *     }
 *     
 *     private fun getSortColumn(sortBy: UserSortField): String {
 *         return when (sortBy) {
 *             UserSortField.NAME -> "name"
 *             UserSortField.EMAIL -> "email"
 *             UserSortField.CREATED_AT -> "created_at"
 *         }
 *     }
 * }
 *
 * // Query handler with read model
 * class GetDashboardStatsQueryHandler(
 *     private val statsReadModel: DashboardStatsReadModel
 * ) : QueryHandler<GetDashboardStatsQuery, DashboardStatsDTO> {
 *     
 *     override fun handle(query: GetDashboardStatsQuery): DashboardStatsDTO {
 *         // Read from pre-computed read model (updated by events)
 *         return statsReadModel.getStats(
 *             userId = query.userId,
 *             fromDate = query.fromDate,
 *             toDate = query.toDate
 *         )
 *     }
 * }
 *
 * // Usage with QueryBus
 * val query = FindUserByIdQuery(userId = "user-123")
 * val user = queryBus.dispatch(query)
 *
 * val productsQuery = ListProductsQuery(
 *     category = "electronics",
 *     minPrice = BigDecimal("100"),
 *     maxPrice = BigDecimal("1000"),
 *     pageNumber = 0,
 *     pageSize = 20
 * )
 * val products = queryBus.dispatch(productsQuery)
 *
 * // Direct usage
 * val handler = FindUserByIdQueryHandler(userRepo)
 * val user = handler.handle(query)
 * ```
 *
 * @param Q The type of query this handler processes
 * @param R The type of result returned
 */
interface QueryHandler<Q : Query<R>, R> {
    /**
     * Handles the given query and returns the result.
     *
     * @param query The query to process
     * @return The query result
     */
    fun handle(query: Q): R
}
