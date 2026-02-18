package com.rodkrtz.foundationkit.valueobject.contact

import com.rodkrtz.foundationkit.valueobject.ValueObject

/**
 * Value object representing a valid email address.
 *
 * This class validates email addresses according to a simplified RFC 5322 regex
 * and enforces a maximum length of 320 characters.
 *
 * @property value The email address string
 * @throws IllegalArgumentException if email is blank, invalid format, or too long
 */
public data class Email(val value: String) : ValueObject {

    init {
        require(value.isNotBlank()) {
            "Email cannot be blank"
        }
        require(EMAIL_REGEX.matches(value)) {
            "Invalid email format: $value"
        }
        require(value.length <= 320) {
            "Email is too long (max 320 characters)"
        }
    }

    /**
     * Returns the domain part of the email (after @).
     *
     * @return The domain string (e.g., "example.com")
     */
    public fun domain(): String = value.substringAfter('@')

    /**
     * Returns the local part of the email (before @).
     *
     * @return The local part string (e.g., "user")
     */
    public fun localPart(): String = value.substringBefore('@')

    /**
     * Returns the email in lowercase for case-insensitive comparison.
     *
     * @return Lowercase version of the email
     */
    public fun normalized(): String = value.lowercase()

    override fun toString(): String = value

    public companion object {
        /** RFC 5322 simplified regex for email validation */
        private val EMAIL_REGEX = Regex(
            """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"""
        )

        /**
         * Attempts to create an Email, returning null if invalid.
         *
         * @param value The email string to parse
         * @return Email instance if valid, null otherwise
         */
        public fun tryParse(value: String): Email? {
            return try {
                Email(value)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
}