package com.rodkrtz.foundationkit.metadata

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MetadataFactoryTest {

    private val metadataFactory = MetadataFactory()

    @Test
    fun `should create metadata with initial version`() {
        val metadata = metadataFactory.create(
            createdBy = "tester",
            source = "api",
            traceId = "trace-1"
        )

        assertThat(metadata.audit.version).isEqualTo(1)
        assertThat(metadata.operation.type).isEqualTo(OperationType.CREATE)
        assertThat(metadata.trace?.traceId).isEqualTo("trace-1")
    }

    @Test
    fun `should update metadata and increment version`() {
        val created = metadataFactory.create(createdBy = "tester", source = "api")
        val updated = metadataFactory.update(created, updatedBy = "tester2", source = "worker")

        assertThat(updated.audit.version).isEqualTo(2)
        assertThat(updated.operation.type).isEqualTo(OperationType.UPDATE)
        assertThat(updated.audit.updatedBy).isEqualTo("tester2")
    }
}
