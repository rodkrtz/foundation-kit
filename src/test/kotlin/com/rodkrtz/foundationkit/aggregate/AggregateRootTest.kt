package com.rodkrtz.foundationkit.aggregate

import com.rodkrtz.foundationkit.event.DomainEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class AggregateRootTest {

    // Define simple ID for testing
    data class TestId(override val value: UUID = UUID.randomUUID()) : AggregateId<UUID>

    // Define simple Event for testing
    data class TestEvent(
        override val eventId: String = UUID.randomUUID().toString(),
        override val occurredOn: Instant = Instant.now(),
        val message: String
    ) : DomainEvent

    // Define Aggregate integration
    class TestAggregate(id: TestId) : AggregateRoot<TestId>() {
        override val id: TestId = id

        fun doSomething(message: String) {
            addDomainEvent(TestEvent(message = message))
        }
    }

    @Test
    fun `should collect domain events`() {
        // Given
        val aggregate = TestAggregate(TestId())

        // When
        aggregate.doSomething("Hello")
        aggregate.doSomething("World")

        // Then
        assertThat(aggregate.getDomainEvents()).hasSize(2)
        assertThat(aggregate.getDomainEvents()[0]).isInstanceOf(TestEvent::class.java)
        assertThat((aggregate.getDomainEvents()[0] as TestEvent).message).isEqualTo("Hello")
    }

    @Test
    fun `should clear domain events`() {
        // Given
        val aggregate = TestAggregate(TestId())
        aggregate.doSomething("Hello")

        // When
        aggregate.clearDomainEvents()

        // Then
        assertThat(aggregate.getDomainEvents()).isEmpty()
    }
}
