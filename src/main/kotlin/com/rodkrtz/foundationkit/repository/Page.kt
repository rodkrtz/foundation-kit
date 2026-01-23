package com.rodkrtz.foundationkit.repository

/**
 * Represents a page of results from a paginated query.
 *
 * This class contains the actual data along with pagination metadata,
 * making it easy to build paginated UIs and APIs.
 *
 * @param T The type of elements in the page
 * @property content The list of elements in this page
 * @property pageNumber The current page number (0-indexed)
 * @property pageSize The number of elements per page
 * @property totalElements The total number of elements across all pages
 */
public data class Page<T>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long
) {
    /**
     * The total number of pages.
     *
     * Calculated as ceil(totalElements / pageSize).
     */
    val totalPages: Int =
        if (pageSize > 0) ((totalElements + pageSize - 1) / pageSize).toInt()
        else 0

    /**
     * Indicates if there is a next page available.
     */
    val hasNext: Boolean = pageNumber < totalPages - 1

    /**
     * Indicates if there is a previous page available.
     */
    val hasPrevious: Boolean = pageNumber > 0

    /**
     * Indicates if this page contains no elements.
     */
    val isEmpty: Boolean = content.isEmpty()
}
