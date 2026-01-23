package com.rodkrtz.foundationkit.query

/**
 * Query Bus for dispatching queries to their handlers.
 *
 * The Query Bus acts as a mediator that routes queries to the appropriate
 * query handlers. It separates the query sender from the handler implementation.
 *
 * Benefits:
 * - Single entry point for all queries
 * - Easy to add caching, logging, monitoring
 * - Simplifies testing with mock handlers
 * - Enables handler registration and discovery
 *
 * Typical usage:
 * ```
 * val query = FindUserByIdQuery(userId = "123")
 * val user = queryBus.dispatch(query)
 * ```
 */
public interface QueryBus {
    /**
     * Dispatches a query to its registered handler.
     *
     * @param Q The query type
     * @param R The result type
     * @param query The query to dispatch
     * @return The result from the query handler
     * @throws IllegalStateException if no handler is registered for the query
     */
    public fun <Q : Query<R>, R> dispatch(query: Q): R
}
