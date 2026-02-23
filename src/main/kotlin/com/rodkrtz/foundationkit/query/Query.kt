package com.rodkrtz.foundationkit.query

/**
 * Marker interface for queries in CQRS pattern.
 *
 * Queries represent read operations that retrieve data without modifying state.
 * They are named with descriptive nouns or verbs indicating what is being queried
 * (e.g., FindUserById, GetActiveOrders, ListProducts).
 *
 * Each query should:
 * - Contain all parameters needed to execute the query
 * - Be immutable (data classes recommended)
 * - Specify the return type as a generic parameter
 * - Be validated before execution
 *
 * Note: Default implementation of queryId regenerates a new UUID on each access.
 * Implementations should override with a stored value.
 *
 * Example usage:
 * ```kotlin
 * // Simple query by ID
 * data class FindUserByIdQuery(
 *     override val queryId: String = UUID.randomUUID().toString(),
 *     val userId: String
 * ) : Query<User?>
 *
 * // Query with multiple parameters
 * data class FindOrdersByCustomerQuery(
 *     override val queryId: String = UUID.randomUUID().toString(),
 *     val customerId: String,
 *     val status: OrderStatus?,
 *     val fromDate: LocalDate?,
 *     val toDate: LocalDate?
 * ) : Query<List<Order>>
 *
 * // Paginated query
 * data class ListProductsQuery(
 *     override val queryId: String = UUID.randomUUID().toString(),
 *     val category: String?,
 *     val minPrice: BigDecimal?,
 *     val maxPrice: BigDecimal?,
 *     val pageNumber: Int = 0,
 *     val pageSize: Int = 20
 * ) : Query<Page<Product>>
 *
 * // Query with sorting
 * data class SearchUsersQuery(
 *     override val queryId: String = UUID.randomUUID().toString(),
 *     val searchTerm: String,
 *     val sortBy: UserSortField = UserSortField.NAME,
 *     val sortDirection: SortDirection = SortDirection.ASC
 * ) : Query<List<User>>
 *
 * enum class UserSortField { NAME, EMAIL, CREATED_AT }
 * enum class SortDirection { ASC, DESC }
 *
 * // Usage with QueryBus
 * val query = FindUserByIdQuery(userId = "user-123")
 * val user = queryBus.dispatch(query)
 *
 * val ordersQuery = FindOrdersByCustomerQuery(
 *     customerId = "customer-456",
 *     status = OrderStatus.COMPLETED,
 *     fromDate = LocalDate.now().minusMonths(1),
 *     toDate = LocalDate.now()
 * )
 * val orders = queryBus.dispatch(ordersQuery)
 *
 * // Usage with QueryHandler directly
 * class FindUserByIdQueryHandler(
 *     private val userRepository: UserRepository
 * ) : QueryHandler<FindUserByIdQuery, User?> {
 *     
 *     override fun handle(query: FindUserByIdQuery): User? {
 *         return userRepository.findById(UserId(query.userId))
 *     }
 * }
 *
 * // Query with DTO projection
 * data class GetUserProfileQuery(
 *     override val queryId: String = UUID.randomUUID().toString(),
 *     val userId: String
 * ) : Query<UserProfileDTO>
 *
 * data class UserProfileDTO(
 *     val id: String,
 *     val name: String,
 *     val email: String,
 *     val avatarUrl: String?,
 *     val memberSince: LocalDate,
 *     val orderCount: Int
 * )
 *
 * class GetUserProfileQueryHandler(
 *     private val jdbcTemplate: JdbcTemplate
 * ) : QueryHandler<GetUserProfileQuery, UserProfileDTO> {
 *     
 *     override fun handle(query: GetUserProfileQuery): UserProfileDTO {
 *         val sql = """
 *             SELECT 
 *                 u.id, u.name, u.email, u.avatar_url, u.created_at,
 *                 COUNT(o.id) as order_count
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
 *                 orderCount = rs.getInt("order_count")
 *             )
 *         }
 *     }
 * }
 *
 * // Query with caching
 * @Cacheable("user-profiles")
 * class CachedGetUserProfileQueryHandler(
 *     private val delegate: GetUserProfileQueryHandler
 * ) : QueryHandler<GetUserProfileQuery, UserProfileDTO> {
 *     
 *     override fun handle(query: GetUserProfileQuery): UserProfileDTO {
 *         return delegate.handle(query)
 *     }
 * }
 *
 * // Query with validation
 * data class GetOrderDetailsQuery(
 *     override val queryId: String = UUID.randomUUID().toString(),
 *     val orderId: String,
 *     val requestedBy: String
 * ) : Query<OrderDetails> {
 *     
 *     init {
 *         require(orderId.isNotBlank()) { "Order ID is required" }
 *         require(requestedBy.isNotBlank()) { "Requested by is required" }
 *     }
 * }
 *
 * // Query with Result
 * fun handle(query: FindUserByEmailQuery): Result<User> {
 *     val user = userRepository.findByEmail(query.email)
 *     return if (user != null) {
 *         Result.success(user)
 *     } else {
 *         Result.failure(NotFoundException("User with email ${query.email} not found"))
 *     }
 * }
 * ```
 *
 * @param R The type of result returned by this query
 */
public interface Query<ID> {
    /**
     * Unique identifier for this query instance.
     *
     * @return Unique query identifier
     */
    public val queryId: ID
}
