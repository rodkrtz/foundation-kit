package com.rodkrtz.foundationkit.valueobject

import java.math.BigDecimal

/**
 * Represents a monetary value with currency.
 *
 * This value object encapsulates an amount and currency, ensuring monetary
 * operations maintain currency consistency and prevent negative values.
 *
 * @property amount The monetary amount (must be non-negative)
 * @property currency ISO 4217 currency code (3 letters, e.g., "USD", "BRL")
 *
 * @throws IllegalArgumentException if amount is negative or currency code is invalid
 */
public data class Money(
    val amount: BigDecimal,
    val currency: String = "BRL"
) : ValueObject {

    init {
        require(amount >= BigDecimal.ZERO) { "Amount cannot be negative" }
        require(currency.length == 3) { "Currency must be ISO 4217 code" }
    }

    /**
     * Adds two Money values with the same currency.
     *
     * @param other The Money to add
     * @return A new Money with the sum of amounts
     * @throws IllegalArgumentException if currencies differ
     */
    public operator fun plus(other: Money): Money {
        requireSameCurrency(other)
        return Money(amount + other.amount, currency)
    }

    /**
     * Subtracts a Money value from this one (same currency required).
     *
     * @param other The Money to subtract
     * @return A new Money with the difference
     * @throws IllegalArgumentException if currencies differ or result would be negative
     */
    public operator fun minus(other: Money): Money {
        requireSameCurrency(other)
        return Money(amount - other.amount, currency)
    }

    /**
     * Multiplies the amount by a BigDecimal multiplier.
     *
     * @param multiplier The multiplier
     * @return A new Money with the multiplied amount
     */
    public operator fun times(multiplier: BigDecimal): Money {
        return Money(amount * multiplier, currency)
    }

    /**
     * Multiplies the amount by an integer multiplier.
     *
     * @param multiplier The multiplier
     * @return A new Money with the multiplied amount
     */
    public operator fun times(multiplier: Int): Money {
        return Money(amount * multiplier.toBigDecimal(), currency)
    }

    /**
     * Validates that two Money objects have the same currency.
     *
     * @throws IllegalArgumentException if currencies differ
     */
    private fun requireSameCurrency(other: Money) {
        require(currency == other.currency) {
            "Cannot operate on different currencies: $currency and ${other.currency}"
        }
    }

    /**
     * Checks if this Money is greater than another (same currency required).
     *
     * @param other The Money to compare
     * @return true if this amount is greater
     * @throws IllegalArgumentException if currencies differ
     */
    public fun isGreaterThan(other: Money): Boolean {
        requireSameCurrency(other)
        return amount > other.amount
    }

    /**
     * Checks if this Money is less than another (same currency required).
     *
     * @param other The Money to compare
     * @return true if this amount is less
     * @throws IllegalArgumentException if currencies differ
     */
    public fun isLessThan(other: Money): Boolean {
        requireSameCurrency(other)
        return amount < other.amount
    }

    public companion object {
        /** Zero money in BRL */
        public val ZERO: Money = Money(BigDecimal.ZERO)

        /**
         * Creates Money from a Double value.
         *
         * @param amount The amount as Double
         * @param currency ISO 4217 currency code (default: "BRL")
         * @return A new Money instance
         */
        public fun of(amount: Double, currency: String = "BRL"): Money =
            Money(amount.toBigDecimal(), currency)

        /**
         * Creates Money from a String value.
         *
         * @param amount The amount as String
         * @param currency ISO 4217 currency code (default: "BRL")
         * @return A new Money instance
         */
        public fun of(amount: String, currency: String = "BRL"): Money =
            Money(BigDecimal(amount), currency)
    }
}
