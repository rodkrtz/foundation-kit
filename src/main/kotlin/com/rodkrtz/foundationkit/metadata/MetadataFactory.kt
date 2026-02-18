package com.rodkrtz.foundationkit.metadata

import java.time.Clock
import java.time.Instant

/**
 * Factory for creating and evolving [Metadata] instances.
 *
 * Keeps metadata transitions consistent across projects that use JSON persistence.
 */
public class MetadataFactory(private val clock: Clock = Clock.systemUTC()) {

    public fun create(
        createdBy: String?,
        source: String,
        traceId: String? = null,
        tenantId: String? = null
    ): Metadata {
        val now = Instant.now(clock)

        return Metadata(
            audit = AuditInfo(
                createdAt = now,
                createdBy = createdBy,
                updatedAt = null,
                updatedBy = null,
                deletedAt = null,
                deletedBy = null,
                version = 1
            ),
            operation = OperationInfo(
                type = OperationType.CREATE,
                source = source,
                tenantId = tenantId
            ),
            trace = TraceInfo(
                requestId = traceId,
                traceId = traceId,
                spanId = null
            ),
            build = null,
            runtime = null,
            request = null
        )
    }

    public fun update(
        current: Metadata,
        updatedBy: String?,
        source: String,
        traceId: String? = null
    ): Metadata {
        val now = Instant.now(clock)

        return current.copy(
            audit = current.audit.copy(
                updatedAt = now,
                updatedBy = updatedBy,
                version = current.audit.version + 1
            ),
            operation = current.operation.copy(
                type = OperationType.UPDATE,
                source = source
            ),
            trace = current.trace?.copy(traceId = traceId) ?: TraceInfo(
                requestId = traceId,
                traceId = traceId,
                spanId = null
            )
        )
    }

    public fun delete(
        current: Metadata,
        deletedBy: String?,
        source: String,
        traceId: String? = null
    ): Metadata {
        val now = Instant.now(clock)

        return current.copy(
            audit = current.audit.copy(
                updatedAt = now,
                updatedBy = deletedBy,
                deletedAt = now,
                deletedBy = deletedBy,
                version = current.audit.version + 1
            ),
            operation = current.operation.copy(
                type = OperationType.DELETE,
                source = source
            ),
            trace = current.trace?.copy(traceId = traceId) ?: TraceInfo(
                requestId = traceId,
                traceId = traceId,
                spanId = null
            )
        )
    }
}

