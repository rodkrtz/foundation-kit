package com.rodkrtz.foundationkit.repository.json

import com.rodkrtz.foundationkit.exception.domain.ConcurrencyException
import com.rodkrtz.foundationkit.metadata.AuditInfo
import com.rodkrtz.foundationkit.metadata.Metadata
import com.rodkrtz.foundationkit.metadata.OperationInfo
import com.rodkrtz.foundationkit.metadata.OperationType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.Instant

class JsonDataTest {

    @Test
    fun `should soft delete when expected version matches`() {
        val jsonData = JsonData(
            id = "id-1",
            data = mapOf("name" to "John"),
            metadata = metadata(version = 2)
        )

        val deleted = jsonData.softDelete(deletedBy = "tester", expectedVersion = 2)

        assertThat(deleted.isDeleted()).isTrue()
        assertThat(deleted.metadata.audit.version).isEqualTo(3)
        assertThat(deleted.metadata.audit.deletedBy).isEqualTo("tester")
    }

    @Test
    fun `should fail soft delete when expected version mismatches`() {
        val jsonData = JsonData(
            id = "id-2",
            data = mapOf("name" to "Jane"),
            metadata = metadata(version = 5)
        )

        assertThatThrownBy {
            jsonData.softDelete(deletedBy = "tester", expectedVersion = 4)
        }
            .isInstanceOf(ConcurrencyException::class.java)
            .hasMessageContaining("Version conflict")
    }

    private fun metadata(version: Long): Metadata {
        return Metadata(
            audit = AuditInfo(
                createdAt = Instant.parse("2026-02-17T10:00:00Z"),
                createdBy = "system",
                updatedAt = null,
                updatedBy = null,
                deletedAt = null,
                deletedBy = null,
                version = version
            ),
            operation = OperationInfo(
                type = OperationType.CREATE,
                source = "test",
                tenantId = null
            ),
            trace = null,
            build = null,
            runtime = null,
            request = null
        )
    }
}
