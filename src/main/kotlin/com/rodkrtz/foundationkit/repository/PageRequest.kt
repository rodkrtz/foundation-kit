package com.rodkrtz.foundationkit.repository

/**
 * Encapsulates pagination parameters for queries.
 *
 * Pagination improves performance and user experience by loading data
 * in manageable chunks instead of all at once.
 *
 * @property pageNumber The page number to retrieve (0-indexed)
 * @property pageSize The number of elements per page
 * @throws IllegalArgumentException if pageNumber < 0, pageSize <= 0, or pageSize > 1000
 */
public data class PageRequest(
    val pageNumber: Int = 0,
    val pageSize: Int = 20
) {
    init {
        require(pageNumber >= 0) { "Page number must be >= 0" }
        require(pageSize > 0) { "Page size must be > 0" }
        require(pageSize <= 1000) { "Page size must be <= 1000" }
    }

    /**
     * The offset to use in database queries.
     *
     * Calculated as pageNumber * pageSize.
     * Useful for SQL OFFSET clauses.
     */
    val offset: Long = pageNumber.toLong() * pageSize
}
