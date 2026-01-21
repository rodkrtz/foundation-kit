package com.rodkrtz.foundationkit.repository.json

/**
 * Generic entity for JSON-based persistence.
 *
 * Represents a row in a table with the following structure:
 * ```sql
 * CREATE TABLE entity_name (
 *     id VARCHAR/UUID PRIMARY KEY,
 *     data JSONB NOT NULL,      -- Domain data as JSON
 *     metadata JSONB NOT NULL    -- Audit and versioning data
 * );
 * ```
 *
 * This pattern separates the identifier and metadata from the domain data,
 * allowing the domain model to remain clean while still supporting features
 * like audit trails, soft deletes, and optimistic locking.
 *
 * Example usage:
 * ```kotlin
 * // Define your domain data class
 * data class UserData(
 *     val email: String,
 *     val name: String,
 *     val role: String,
 *     val active: Boolean
 * )
 *
 * // Create a new entity
 * val userId = UUID.randomUUID()
 * val userData = UserData(
 *     email = "john@example.com",
 *     name = "John Doe",
 *     role = "ADMIN",
 *     active = true
 * )
 * val metadata = Metadata.create(createdBy = "system")
 * 
 * val jsonData = JsonData(
 *     id = userId,
 *     data = userData,
 *     metadata = metadata
 * )
 *
 * // Update the data
 * val updatedData = userData.copy(name = "John Smith")
 * val updated = jsonData.updateData(updatedData, updatedBy = "admin")
 *
 * println(updated.data.name)       // "John Smith"
 * println(updated.metadata.version) // 2
 * println(updated.metadata.updatedBy) // "admin"
 *
 * // Soft delete
 * val deleted = updated.softDelete(deletedBy = "admin")
 * println(deleted.isDeleted())     // true
 * println(deleted.metadata.deleted) // true
 * println(deleted.metadata.deletedAt) // 2026-01-21T...
 *
 * // Check version for optimistic locking
 * val currentVersion = jsonData.getVersion() // 1
 * if (currentVersion != expectedVersion) {
 *     throw ConcurrencyException("Data was modified by another user")
 * }
 *
 * // With tags for categorization
 * val metadata = Metadata.create(createdBy = "system").copy(
 *     tags = mapOf(
 *         "department" to "IT",
 *         "priority" to "high",
 *         "project" to "migration"
 *     )
 * )
 *
 * // Repository usage
 * class UserJsonRepository(
 *     private val jdbcTemplate: JdbcTemplate,
 *     private val jsonSerializer: JsonSerializer
 * ) : JsonRepositorySupport<UUID, UserData>() {
 *     
 *     override fun save(entity: JsonData<UUID, UserData>): JsonData<UUID, UserData> {
 *         val sql = """
 *             INSERT INTO users (id, data, metadata) 
 *             VALUES (?, ?::jsonb, ?::jsonb)
 *             ON CONFLICT (id) 
 *             DO UPDATE SET 
 *                 data = EXCLUDED.data,
 *                 metadata = EXCLUDED.metadata
 *         """
 *         
 *         jdbcTemplate.update(
 *             sql,
 *             entity.id,
 *             jsonSerializer.serialize(entity.data),
 *             jsonSerializer.serialize(entity.metadata)
 *         )
 *         
 *         return entity
 *     }
 *     
 *     override fun findById(id: UUID): JsonData<UUID, UserData>? {
 *         val sql = "SELECT id, data, metadata FROM users WHERE id = ?"
 *         
 *         return jdbcTemplate.queryForObject(sql, id) { rs, _ ->
 *             JsonData(
 *                 id = UUID.fromString(rs.getString("id")),
 *                 data = jsonSerializer.deserialize(rs.getString("data"), UserData::class.java),
 *                 metadata = jsonSerializer.deserialize(rs.getString("metadata"), Metadata::class.java)
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * @param ID The type of the identifier
 * @param DATA The type of the domain data
 * @property id Unique identifier for this entity
 * @property data Domain-specific data stored as JSON
 * @property metadata Audit and versioning information
 */
data class JsonData<ID, DATA>(
    val id: ID,
    val data: DATA,
    val metadata: Metadata
) {
    /**
     * Creates a new JsonData with updated domain data.
     *
     * Increments the version and updates the timestamp. Optionally records
     * who performed the update.
     *
     * @param newData The new domain data
     * @param updatedBy Optional identifier of who updated the data
     * @return New JsonData with updated data and metadata
     */
    fun updateData(newData: DATA, updatedBy: String? = null): JsonData<ID, DATA> {
        return copy(
            data = newData,
            metadata = metadata.incrementVersion().let {
                if (updatedBy != null) it.updateBy(updatedBy) else it
            }
        )
    }

    /**
     * Marks this entity as soft-deleted.
     *
     * Sets the deleted flag in metadata without actually removing the record.
     * The entity can still be queried but will be excluded from normal queries.
     *
     * @param deletedBy Optional identifier of who deleted the data
     * @return New JsonData marked as deleted
     */
    fun softDelete(deletedBy: String? = null): JsonData<ID, DATA> {
        return copy(
            metadata = metadata.markAsDeleted(deletedBy)
        )
    }

    /**
     * Checks if this entity is soft-deleted.
     *
     * @return true if marked as deleted
     */
    fun isDeleted(): Boolean = metadata.deleted

    /**
     * Gets the current version number for optimistic locking.
     *
     * @return Current version number
     */
    fun getVersion(): Long = metadata.version
}
