package com.rodkrtz.foundationkit.repository.json

import com.rodkrtz.foundationkit.metadata.Metadata

/**
 * Abstract base class with common implementations for JsonRepository.
 *
 * This class provides default implementations for common operations,
 * allowing concrete implementations to focus only on persistence logic.
 *
 * Concrete implementations must implement:
 * - save(entity)
 * - findById(id)
 * - findAll(includeDeleted)
 * - findAll(pageRequest, includeDeleted)
 * - existsById(id)
 * - deleteById(id)
 * - findByJsonPath(jsonPath)
 *
 * Example implementation:
 * ```
 * class UserJsonRepository(private val jdbcTemplate: JdbcTemplate) 
 *     : JsonRepositorySupport<UUID, UserData>() {
 *
 *     override fun save(entity: JsonData<UUID, UserData>): JsonData<UUID, UserData> {
 *         jdbcTemplate.update("INSERT INTO users (id, data, metadata) VALUES (?, ?, ?)",
 *             entity.id, toJson(entity.data), toJson(entity.metadata))
 *         return entity
 *     }
 *     // ... implement other methods
 * }
 * ```
 *
 * @param ID The type of the identifier
 * @param DATA The type of the domain data
 */
public abstract class JsonRepositorySupport<ID, DATA> : JsonRepository<ID, DATA> {

    /**
     * Performs a soft delete by marking the entity as deleted.
     *
     * The entity remains in the database but is excluded from normal queries.
     * This default implementation uses findById and save.
     *
     * @param id The identifier of the entity to soft delete
     * @param deletedBy Optional identifier of who performed the deletion
     * @param expectedVersion Optional expected version for optimistic locking checks
     * @return The soft-deleted entity if found, null otherwise
     */
    override fun softDeleteById(
        id: ID,
        deletedBy: String?,
        expectedVersion: Long?
    ): JsonData<ID, DATA>? {
        val entity = findById(id) ?: return null
        val deletedEntity = entity.softDelete(deletedBy, expectedVersion)
        return save(deletedEntity)
    }

    /**
     * Finds entities by applying a predicate to their metadata.
     *
     * This default implementation loads all entities (including deleted)
     * and filters in memory. Override for better performance with
     * database-specific queries.
     *
     * @param predicate Function that tests metadata
     * @return List of entities whose metadata matches the predicate
     */
    override fun findByMetadata(predicate: (Metadata) -> Boolean): List<JsonData<ID, DATA>> {
        throw UnsupportedOperationException(
            "findByMetadata default implementation was removed. " +
                "Override this method using repository-specific indexed queries."
        )
    }
}
