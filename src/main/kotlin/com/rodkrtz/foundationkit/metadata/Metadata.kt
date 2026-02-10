package com.rodkrtz.foundationkit.metadata

import java.time.Instant

public data class Metadata(
    val audit: AuditInfo,
    val operation: OperationInfo,
    val trace: TraceInfo?,
    val build: BuildInfo?,
    val runtime: RuntimeInfo?,
    val request: RequestInfo?
)

public enum class OperationType {
    CREATE, UPDATE, DELETE
}

public data class AuditInfo(
    val createdAt: Instant,
    val createdBy: String?,
    val updatedAt: Instant?,
    val updatedBy: String?,
    val deletedAt: Instant?,
    val deletedBy: String?,
    val version: Long
)

public data class OperationInfo(
    val type: OperationType,
    val source: String?,     // api / worker / job / migration
    val tenantId: String?
)

public data class TraceInfo(
    val requestId: String?,
    val traceId: String?,
    val spanId: String?
)

public data class BuildInfo(
    val apiName: String?,
    val apiVersion: String?,
    val gitCommit: String?,
    val buildTime: Instant?
)

public data class RuntimeInfo(
    val environment: String?,     // prod / staging / dev
    val serviceInstance: String?, // pod / container / hostname
    val ipAddress: String?
)

public data class RequestInfo(
    val userAgent: String?
)