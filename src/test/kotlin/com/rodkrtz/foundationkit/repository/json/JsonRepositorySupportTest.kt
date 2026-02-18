package com.rodkrtz.foundationkit.repository.json

import com.rodkrtz.foundationkit.metadata.AuditInfo
import com.rodkrtz.foundationkit.metadata.Metadata
import com.rodkrtz.foundationkit.metadata.OperationInfo
import com.rodkrtz.foundationkit.metadata.OperationType
import com.rodkrtz.foundationkit.repository.Page
import com.rodkrtz.foundationkit.repository.PageRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.Instant

class JsonRepositorySupportTest {

    @Test
    fun `should throw for metadata query when not overridden`() {
        val repository = InMemoryRepository()

        assertThatThrownBy {
            repository.findByMetadata { true }
        }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessageContaining("default implementation was removed")
    }

    @Test
    fun `should soft delete with optimistic locking`() {
        val repository = InMemoryRepository()
        val current = JsonData(
            id = "item-1",
            data = mapOf("status" to "ACTIVE"),
            metadata = metadata(version = 1)
        )

        repository.save(current)
        val deleted = repository.softDeleteById("item-1", deletedBy = "tester", expectedVersion = 1)

        assertThat(deleted).isNotNull
        assertThat(deleted!!.metadata.audit.version).isEqualTo(2)
        assertThat(deleted.isDeleted()).isTrue()
    }

    private class InMemoryRepository : JsonRepositorySupport<String, Map<String, String>>() {
        private val store = mutableMapOf<String, JsonData<String, Map<String, String>>>()

        override fun save(entity: JsonData<String, Map<String, String>>): JsonData<String, Map<String, String>> {
            store[entity.id] = entity
            return entity
        }

        override fun findById(id: String): JsonData<String, Map<String, String>>? = store[id]

        override fun findAll(includeDeleted: Boolean): List<JsonData<String, Map<String, String>>> {
            return store.values.toList()
        }

        override fun findAll(
            pageRequest: PageRequest,
            includeDeleted: Boolean
        ): Page<JsonData<String, Map<String, String>>> {
            val values = store.values.toList()
            return Page(
                content = values,
                pageNumber = pageRequest.pageNumber,
                pageSize = pageRequest.pageSize,
                totalElements = values.size.toLong()
            )
        }

        override fun existsById(id: String): Boolean = store.containsKey(id)

        override fun deleteById(id: String) {
            store.remove(id)
        }

        @Deprecated("Kept only for compatibility with JsonRepository contract tests")
        override fun findByJsonPath(jsonPath: String): List<JsonData<String, Map<String, String>>> {
            return store.values.toList()
        }
    }

    private companion object {
        fun metadata(version: Long): Metadata = Metadata(
            audit = AuditInfo(
                createdAt = Instant.parse("2026-02-17T10:00:00Z"),
                createdBy = "seed",
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
