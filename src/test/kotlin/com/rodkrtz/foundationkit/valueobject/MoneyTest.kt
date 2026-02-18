package com.rodkrtz.foundationkit.valueobject

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class MoneyTest {

    @Test
    fun `should create money with valid amount and currency`() {
        // Given
        val amount = BigDecimal("100.50")
        val currency = "USD"

        // When
        val money = Money(amount, currency)

        // Then
        assertThat(money.amount).isEqualTo(amount)
        assertThat(money.currency).isEqualTo(currency)
    }

    @Test
    fun `should fail when amount is negative`() {
        assertThatThrownBy {
            Money(BigDecimal("-10.00"))
        }.isInstanceOf(IllegalArgumentException::class.java)
         .hasMessage("Amount cannot be negative")
    }

    @Test
    fun `should add money with same currency`() {
        // Given
        val moneyTF = Money.of(10.00)
        val moneyTwenty = Money.of(20.00)

        // When
        val result = moneyTF + moneyTwenty

        // Then
        assertThat(result.amount).isEqualByComparingTo(BigDecimal("30.00"))
        assertThat(result.currency).isEqualTo("BRL")
    }

    @Test
    fun `should fail to add money with different currencies`() {
        // Given
        val brl = Money.of(10.00, "BRL")
        val usd = Money.of(10.00, "USD")

        // When / Then
        assertThatThrownBy {
            brl + usd
        }.isInstanceOf(IllegalArgumentException::class.java)
         .hasMessageContaining("Cannot operate on different currencies")
    }
}
