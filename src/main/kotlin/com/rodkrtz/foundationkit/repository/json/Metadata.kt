package com.rodkrtz.foundationkit.repository.json

import java.time.Instant

/**
 * Standard metadata for JSON-persisted entities.
 *
 * This class encapsulates audit information, versioning, and soft delete functionality
 * for entities stored in JSON format.
 *
 * @property createdAt Timestamp when the entity was created
 * @property updatedAt Timestamp of the last update
 * @property createdBy Optional identifier of the user who created the entity
 * @property updatedBy Optional identifier of the user who last updated the entity
 * @property version Version number for optimistic locking (starts at 1)
 * @property tags Flexible key-value pairs for categorization and filtering
 * @property deleted Flag indicating if the entity is soft-deleted
 * @property deletedAt Timestamp when the entity was soft-deleted (null if not deleted)
 */
public data class Metadata(
    val createdAt: Instant,
    val updatedAt: Instant,
    val createdBy: String? = null,
    val updatedBy: String? = null,
    val version: Long = 1,
    val tags: Map<String, String> = emptyMap(),
    val deleted: Boolean = false,
    val deletedAt: Instant? = null
) {
    /**
     * Creates a new Metadata with incremented version and updated timestamp.
     *
     * Used for optimistic locking when updating entities.
     *
     * @return New Metadata with version incremented by 1
     */
    public fun incrementVersion(): Metadata = copy(
        version = version + 1,
        updatedAt = Instant.now()
    )

    /**
     * Marks this metadata as deleted (soft delete).
     *
     * Sets the deleted flag to true and records the deletion timestamp.
     *
     * @param deletedBy Optional identifier of the user performing the deletion
     * @return New Metadata marked as deleted
     */
    public fun markAsDeleted(deletedBy: String? = null): Metadata = copy(
        deleted = true,
        deletedAt = Instant.now(),
        updatedBy = deletedBy
    )

    /**
     * Updates the metadata with a new updatedBy user and current timestamp.
     *
     * @param userId Identifier of the user performing the update
     * @return New Metadata with updated fields
     */
    public fun updateBy(userId: String): Metadata = copy(
        updatedAt = Instant.now(),
        updatedBy = userId
    )

    public companion object {
        /**
         * Factory method to create initial metadata for a new entity.
         *
         * Sets both created and updated timestamps to now, and initializes version to 1.
         *
         * @param createdBy Optional identifier of the user creating the entity
         * @return New Metadata instance for a new entity
         */
        public fun create(createdBy: String? = null): Metadata = Metadata(
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            createdBy = createdBy,
            version = 1
        )
    }
}
