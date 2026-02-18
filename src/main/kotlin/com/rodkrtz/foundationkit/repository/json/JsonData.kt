package com.rodkrtz.foundationkit.repository.json

import com.rodkrtz.foundationkit.exception.domain.ConcurrencyException
import com.rodkrtz.foundationkit.metadata.Metadata
import com.rodkrtz.foundationkit.metadata.OperationType
import java.time.Instant

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
 * val metadata = Metadata(
 *     audit = AuditInfo(
 *         createdAt = Instant.now(),
 *         createdBy = "system",
 *         updatedAt = null,
 *         updatedBy = null,
 *         deletedAt = null,
 *         deletedBy = null,
 *         version = 1
 *     ),
 *     operation = OperationInfo(
 *         type = OperationType.CREATE,
 *         source = "api",
 *         tenantId = null
 *     ),
 *     trace = null,
 *     build = null,
 *     runtime = null,
 *     request = null
 * )
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
 * println(updated.metadata.audit.version) // 2
 * println(updated.metadata.audit.updatedBy) // "admin"
 *
 * // Soft delete
 * val deleted = updated.softDelete(deletedBy = "admin")
 * println(deleted.isDeleted())     // true
 * println(deleted.metadata.audit.deletedAt) // 2026-01-21T...
 *
 * // Check version for optimistic locking
 * val currentVersion = jsonData.getVersion() // 1
 * if (currentVersion != expectedVersion) {
 *     throw ConcurrencyException("Data was modified by another user")
 * }
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
public data class JsonData<ID, DATA>(
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
    public fun updateData(newData: DATA, updatedBy: String? = null): JsonData<ID, DATA> {
        val now = Instant.now()
        val updatedAudit = metadata.audit.copy(
            updatedAt = now,
            updatedBy = updatedBy ?: metadata.audit.updatedBy,
            version = metadata.audit.version + 1
        )
        val updatedOperation = metadata.operation.copy(type = OperationType.UPDATE)

        return copy(
            data = newData,
            metadata = metadata.copy(
                audit = updatedAudit,
                operation = updatedOperation
            )
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
    public fun softDelete(
        deletedBy: String? = null,
        expectedVersion: Long? = null
    ): JsonData<ID, DATA> {
        if (expectedVersion != null && expectedVersion != metadata.audit.version) {
            throw ConcurrencyException(
                message = "Version conflict while soft deleting json entity",
                expectedVersion = expectedVersion,
                actualVersion = metadata.audit.version
            )
        }

        val now = Instant.now()
        val deletedAudit = metadata.audit.copy(
            deletedAt = now,
            deletedBy = deletedBy ?: metadata.audit.deletedBy,
            updatedAt = now,
            updatedBy = deletedBy ?: metadata.audit.updatedBy,
            version = metadata.audit.version + 1
        )
        val deletedOperation = metadata.operation.copy(type = OperationType.DELETE)

        return copy(
            metadata = metadata.copy(
                audit = deletedAudit,
                operation = deletedOperation
            )
        )
    }

    /**
     * Checks if this entity is soft-deleted.
     *
     * @return true if marked as deleted
     */
    public fun isDeleted(): Boolean = metadata.audit.deletedAt != null

    /**
     * Gets the current version number for optimistic locking.
     *
     * @return Current version number
     */
    public fun getVersion(): Long = metadata.audit.version
}
