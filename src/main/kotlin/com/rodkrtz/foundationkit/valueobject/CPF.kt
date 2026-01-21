package com.rodkrtz.foundationkit.valueobject

/**
 * Value object representing a valid Brazilian CPF (Cadastro de Pessoas Físicas).
 *
 * CPF is an 11-digit number used to identify individuals in Brazil.
 * This class validates the check digits and accepts both formatted and unformatted input.
 *
 * Accepted formats:
 * - 123.456.789-00 (formatted)
 * - 12345678900 (unformatted)
 *
 * @property value The CPF string (formatted or unformatted)
 * @throws IllegalArgumentException if CPF is invalid
 */
data class CPF(val value: String) : ValueObject {

    private val cleanedValue: String

    init {
        cleanedValue = value.replace(Regex("[^0-9]"), "")

        require(cleanedValue.length == 11) {
            "CPF must have 11 digits. Got: ${cleanedValue.length}"
        }

        require(!isAllSameDigit(cleanedValue)) {
            "CPF cannot have all same digits"
        }

        require(isValid(cleanedValue)) {
            "Invalid CPF: $value"
        }
    }

    /**
     * Returns the CPF in formatted style: 123.456.789-00
     *
     * @return Formatted CPF string
     */
    fun formatted(): String {
        return "${cleanedValue.substring(0, 3)}.${cleanedValue.substring(3, 6)}.${cleanedValue.substring(6, 9)}-${cleanedValue.substring(9, 11)}"
    }

    /**
     * Returns only the digits of the CPF.
     *
     * @return CPF with only digits (11 characters)
     */
    fun digitsOnly(): String = cleanedValue

    override fun toString(): String = formatted()

    companion object {
        /**
         * Checks if all digits in the CPF are the same.
         * CPFs like 111.111.111-11 are invalid.
         */
        private fun isAllSameDigit(cpf: String): Boolean {
            return cpf.all { it == cpf[0] }
        }

        /**
         * Validates a CPF by checking its check digits.
         *
         * @param cpf The CPF string with only digits
         * @return true if the CPF is valid
         */
        private fun isValid(cpf: String): Boolean {
            // Calculate first check digit
            var sum = 0
            for (i in 0..8) {
                sum += cpf[i].digitToInt() * (10 - i)
            }
            var remainder = sum % 11
            val firstVerifier = if (remainder < 2) 0 else 11 - remainder

            if (cpf[9].digitToInt() != firstVerifier) {
                return false
            }

            // Calculate second check digit
            sum = 0
            for (i in 0..9) {
                sum += cpf[i].digitToInt() * (11 - i)
            }
            remainder = sum % 11
            val secondVerifier = if (remainder < 2) 0 else 11 - remainder

            return cpf[10].digitToInt() == secondVerifier
        }

        /**
         * Attempts to create a CPF, returning null if invalid.
         *
         * @param value The CPF string to parse
         * @return CPF instance if valid, null otherwise
         */
        fun tryParse(value: String): CPF? {
            return try {
                CPF(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        /**
         * Generates a valid random CPF (useful for testing).
         *
         * @return A valid random CPF instance
         */
        fun random(): CPF {
            val random = java.util.Random()
            val base = (0..8).map { random.nextInt(10) }.joinToString("")

            // Calculate check digits
            var sum = 0
            for (i in 0..8) {
                sum += base[i].digitToInt() * (10 - i)
            }
            var remainder = sum % 11
            val firstVerifier = if (remainder < 2) 0 else 11 - remainder

            sum = 0
            val withFirst = base + firstVerifier
            for (i in 0..9) {
                sum += withFirst[i].digitToInt() * (11 - i)
            }
            remainder = sum % 11
            val secondVerifier = if (remainder < 2) 0 else 11 - remainder

            return CPF(base + firstVerifier.toString() + secondVerifier.toString())
        }
    }
}
