package com.rodkrtz.foundationkit.repository.json

import com.rodkrtz.foundationkit.metadata.Metadata
import com.rodkrtz.foundationkit.repository.Page
import com.rodkrtz.foundationkit.repository.PageRequest

/**
 * Repository for entities persisted as JSON.
 *
 * Expected table structure:
 * ```sql
 * CREATE TABLE entity_name (
 *     id VARCHAR/UUID PRIMARY KEY,
 *     data JSONB NOT NULL,
 *     metadata JSONB NOT NULL
 * );
 * ```
 *
 * This repository provides CRUD operations for JSON-persisted data along with
 * soft delete capabilities and JSONPath querying support.
 *
 * @param ID Type of the entity identifier
 * @param DATA Type of the domain data stored in JSON format
 */
public interface JsonRepository<ID, DATA> {

    /**
     * Saves or updates an entity.
     *
     * @param entity The JSON data entity to save
     * @return The saved entity (may include generated values)
     */
    public fun save(entity: JsonData<ID, DATA>): JsonData<ID, DATA>

    /**
     * Finds an entity by its identifier.
     *
     * @param id The entity identifier
     * @return The entity if found, null otherwise
     */
    public fun findById(id: ID): JsonData<ID, DATA>?

    /**
     * Finds all entities (excluding deleted by default).
     *
     * @param includeDeleted If true, includes soft-deleted entities
     * @return List of all entities matching the criteria
     */
    public fun findAll(includeDeleted: Boolean = false): List<JsonData<ID, DATA>>

    /**
     * Finds entities with pagination (excluding deleted by default).
     *
     * @param pageRequest Pagination parameters
     * @param includeDeleted If true, includes soft-deleted entities
     * @return Paginated result containing entities and pagination metadata
     */
    public fun findAll(pageRequest: PageRequest, includeDeleted: Boolean = false): Page<JsonData<ID, DATA>>

    /**
     * Checks if an entity exists by its identifier.
     *
     * @param id The entity identifier
     * @return true if entity exists, false otherwise
     */
    public fun existsById(id: ID): Boolean

    /**
     * Physically deletes an entity from the database.
     *
     * @param id The entity identifier to delete
     */
    public fun deleteById(id: ID)

    /**
     * Soft deletes an entity by marking it as deleted in metadata.
     *
     * The entity remains in the database but is excluded from normal queries.
     *
     * @param id The entity identifier to soft delete
     * @param deletedBy Optional user identifier who performed the deletion
     * @return The soft-deleted entity if found, null otherwise
     */
    public fun softDeleteById(id: ID, deletedBy: String? = null): JsonData<ID, DATA>?

    /**
     * Finds entities using a JSONPath query.
     *
     * JSONPath syntax is database-specific:
     * - PostgreSQL example: `"data->>'status' = 'ACTIVE'"`
     * - MySQL example: `"JSON_EXTRACT(data, '$.status') = 'ACTIVE'"`
     *
     * @param jsonPath The JSONPath query string (database-specific syntax)
     * @return List of entities matching the query
     */
    public fun findByJsonPath(jsonPath: String): List<JsonData<ID, DATA>>

    /**
     * Finds entities by applying a predicate to their metadata.
     *
     * @param predicate Function that tests metadata for matching criteria
     * @return List of entities whose metadata matches the predicate
     */
    public fun findByMetadata(predicate: (Metadata) -> Boolean): List<JsonData<ID, DATA>>
}
