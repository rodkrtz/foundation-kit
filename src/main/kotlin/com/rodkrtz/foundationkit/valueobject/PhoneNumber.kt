package com.rodkrtz.foundationkit.valueobject

/**
 * Value object representing a Brazilian phone number.
 *
 * Accepts formats with or without area code (DDD), with or without formatting.
 * Validates area codes against official Brazilian DDDs and enforces mobile number rules.
 *
 * Valid examples:
 * - (11) 98765-4321 (mobile, formatted)
 * - 11987654321 (mobile, unformatted)
 * - (11) 3456-7890 (landline, formatted)
 * - 1134567890 (landline, unformatted)
 *
 * @property value The phone number string
 * @throws IllegalArgumentException if phone number is invalid
 */
data class PhoneNumber(val value: String) : ValueObject {

    private val cleanedNumber: String

    init {
        cleanedNumber = value.replace(Regex("[^0-9]"), "")

        require(cleanedNumber.isNotBlank()) {
            "Phone number cannot be blank"
        }

        require(cleanedNumber.length in 10..11) {
            "Phone number must have 10 or 11 digits (with DDD). Got: ${cleanedNumber.length} digits"
        }

        // Validate area code (first 2 digits)
        val ddd = cleanedNumber.take(2).toIntOrNull()
        require(ddd != null && isValidDDD(ddd)) {
            "Invalid DDD: $ddd"
        }

        // If 11 digits, third digit must be 9 (mobile)
        if (cleanedNumber.length == 11) {
            require(cleanedNumber[2] == '9') {
                "Mobile number must have 9 as third digit"
            }
        }
    }

    /**
     * Returns the area code (DDD).
     *
     * @return Two-digit area code string
     */
    fun ddd(): String = cleanedNumber.take(2)

    /**
     * Returns the number without area code.
     *
     * @return Phone number without DDD (8 or 9 digits)
     */
    fun number(): String = cleanedNumber.drop(2)

    /**
     * Checks if this is a mobile number (11 digits total).
     *
     * @return true if mobile, false if landline
     */
    fun isMobile(): Boolean = cleanedNumber.length == 11

    /**
     * Returns the formatted phone number.
     *
     * Mobile: (11) 98765-4321
     * Landline: (11) 3456-7890
     *
     * @return Formatted phone number string
     */
    fun formatted(): String {
        return if (isMobile()) {
            "(${ddd()}) ${number().take(5)}-${number().drop(5)}"
        } else {
            "(${ddd()}) ${number().take(4)}-${number().drop(4)}"
        }
    }

    /**
     * Returns only the digits of the phone number.
     *
     * @return Phone number with only digits (10 or 11 characters)
     */
    fun digitsOnly(): String = cleanedNumber

    override fun toString(): String = formatted()

    companion object {
        /** Valid area codes (DDDs) in Brazil */
        private val VALID_DDDS = setOf(
            11, 12, 13, 14, 15, 16, 17, 18, 19, // SP
            21, 22, 24, // RJ
            27, 28, // ES
            31, 32, 33, 34, 35, 37, 38, // MG
            41, 42, 43, 44, 45, 46, // PR
            47, 48, 49, // SC
            51, 53, 54, 55, // RS
            61, // DF
            62, 64, // GO
            63, // TO
            65, 66, // MT
            67, // MS
            68, // AC
            69, // RO
            71, 73, 74, 75, 77, // BA
            79, // SE
            81, 87, // PE
            82, // AL
            83, // PB
            84, // RN
            85, 88, // CE
            86, 89, // PI
            91, 93, 94, // PA
            92, 97, // AM
            95, // RR
            96, // AP
            98, 99  // MA
        )

        /**
         * Checks if an area code is valid in Brazil.
         */
        private fun isValidDDD(ddd: Int): Boolean = ddd in VALID_DDDS

        /**
         * Attempts to create a PhoneNumber, returning null if invalid.
         *
         * @param value The phone number string to parse
         * @return PhoneNumber instance if valid, null otherwise
         */
        fun tryParse(value: String): PhoneNumber? {
            return try {
                PhoneNumber(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
