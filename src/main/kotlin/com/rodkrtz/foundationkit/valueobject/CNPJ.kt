package com.rodkrtz.foundationkit.valueobject

/**
 * Value object representing a valid Brazilian CNPJ (Cadastro Nacional da Pessoa Jurídica).
 *
 * CNPJ is a 14-digit number used to identify legal entities (companies) in Brazil.
 * This class validates the check digits and accepts both formatted and unformatted input.
 *
 * Accepted formats:
 * - 12.345.678/0001-00 (formatted)
 * - 12345678000100 (unformatted)
 *
 * @property value The CNPJ string (formatted or unformatted)
 * @throws IllegalArgumentException if CNPJ is invalid
 */
public data class CNPJ(val value: String) : ValueObject {

    private val cleanedValue: String = value.replace(Regex("[^0-9]"), "")

    init {
        require(cleanedValue.length == 14) {
            "CNPJ must have 14 digits. Got: ${cleanedValue.length}"
        }

        require(!isAllSameDigit(cleanedValue)) {
            "CNPJ cannot have all same digits"
        }

        require(isValid(cleanedValue)) {
            "Invalid CNPJ: $value"
        }
    }

    /**
     * Returns the CNPJ in formatted style: 12.345.678/0001-00
     *
     * @return Formatted CNPJ string
     */
    public fun formatted(): String {
        return "${cleanedValue.substring(0, 2)}.${cleanedValue.substring(2, 5)}.${cleanedValue.substring(5, 8)}/${cleanedValue.substring(8, 12)}-${cleanedValue.substring(12, 14)}"
    }

    /**
     * Returns only the digits of the CNPJ.
     *
     * @return CNPJ with only digits (14 characters)
     */
    public fun digitsOnly(): String = cleanedValue

    /**
     * Returns the root of the CNPJ (first 8 digits).
     *
     * The root identifies the company, while branch identifies the specific location.
     *
     * @return Root string (8 digits)
     */
    public fun root(): String = cleanedValue.substring(0, 8)

    /**
     * Returns the branch number (4 digits after root).
     *
     * The branch number identifies a specific location of the company.
     * 0001 typically indicates the headquarters.
     *
     * @return Branch string (4 digits)
     */
    public fun branch(): String = cleanedValue.substring(8, 12)

    /**
     * Checks if this CNPJ represents the company headquarters (branch 0001).
     *
     * @return true if this is the headquarters
     */
    public fun isHeadquarters(): Boolean = branch() == "0001"

    override fun toString(): String = formatted()

    public companion object {
        /**
         * Checks if all digits in the CNPJ are the same.
         * CNPJs like 11.111.111/1111-11 are invalid.
         */
        private fun isAllSameDigit(cnpj: String): Boolean {
            return cnpj.all { it == cnpj[0] }
        }

        /**
         * Validates a CNPJ by checking its check digits.
         *
         * @param cnpj The CNPJ string with only digits
         * @return true if the CNPJ is valid
         */
        private fun isValid(cnpj: String): Boolean {
            // Calculate first check digit
            val weights1 = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
            var sum = 0
            for (i in 0..11) {
                sum += cnpj[i].digitToInt() * weights1[i]
            }
            var remainder = sum % 11
            val firstVerifier = if (remainder < 2) 0 else 11 - remainder

            if (cnpj[12].digitToInt() != firstVerifier) {
                return false
            }

            // Calculate second check digit
            val weights2 = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
            sum = 0
            for (i in 0..12) {
                sum += cnpj[i].digitToInt() * weights2[i]
            }
            remainder = sum % 11
            val secondVerifier = if (remainder < 2) 0 else 11 - remainder

            return cnpj[13].digitToInt() == secondVerifier
        }

        /**
         * Attempts to create a CNPJ, returning null if invalid.
         *
         * @param value The CNPJ string to parse
         * @return CNPJ instance if valid, null otherwise
         */
        public fun tryParse(value: String): CNPJ? {
            return try {
                CNPJ(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        /**
         * Generates a valid random CNPJ (useful for testing).
         *
         * @return A valid random CNPJ instance
         */
        public fun random(): CNPJ {
            val random = java.util.Random()
            val base = (0..11).map { random.nextInt(10) }.joinToString("")

            // Calculate check digits
            val weights1 = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
            var sum = 0
            for (i in 0..11) {
                sum += base[i].digitToInt() * weights1[i]
            }
            var remainder = sum % 11
            val firstVerifier = if (remainder < 2) 0 else 11 - remainder

            val weights2 = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
            sum = 0
            val withFirst = base + firstVerifier
            for (i in 0..12) {
                sum += withFirst[i].digitToInt() * weights2[i]
            }
            remainder = sum % 11
            val secondVerifier = if (remainder < 2) 0 else 11 - remainder

            return CNPJ(base + firstVerifier.toString() + secondVerifier.toString())
        }
    }
}
