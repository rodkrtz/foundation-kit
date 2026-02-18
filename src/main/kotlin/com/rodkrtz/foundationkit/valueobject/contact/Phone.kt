package com.rodkrtz.foundationkit.valueobject.contact

import java.security.MessageDigest

@JvmInline
public value class Phone private constructor(public val e164: String) {

    override fun toString(): String = e164

    public val digits: String get() = e164.substring(1)
    public val countryCode: String get() = "55"
    public val areaCode: String get() = digits.substring(2, 4)
    public val localNumber: String get() = digits.substring(4)

    public val isMobile: Boolean get() = localNumber.length == 9 && localNumber[0] == '9'
    public val isLandline: Boolean get() = localNumber.length == 8

    public val masked: String
        get() = "+55******" + localNumber.takeLast(4)

    public val formatted: String
        get() = if (isMobile)
            "($areaCode) ${localNumber.substring(0, 5)}-${localNumber.substring(5)}"
        else
            "($areaCode) ${localNumber.substring(0, 4)}-${localNumber.substring(4)}"

    public fun fingerprint(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(e164.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    public companion object {

        private const val COUNTRY = "55"

        public fun parse(raw: CharSequence?): Phone? {
            val digits = onlyDigits(raw) ?: return null
            return fromDigitsOrNull(digits)
        }

        public fun isValid(raw: CharSequence?): Boolean =
            parse(raw) != null

        public fun fromE164(e164: String): Phone {
            require(isValidE164(e164)) { "Invalid E164 phone: $e164" }
            return Phone(e164)
        }

        private fun fromDigitsOrNull(d: String): Phone? {

            val digits = when {
                d.startsWith("55") && d.length >= 12 -> d
                d.length == 10 || d.length == 11 -> COUNTRY + d
                d.startsWith("0") && (d.length == 12) -> COUNTRY + d.substring(1)
                else -> return null
            }

            if (digits.length !in 12..13) return null

            val ddd = digits.substring(2, 4)
            if (!DDD_TABLE.contains(ddd)) return null

            val local = digits.substring(4)

            val validLocal =
                (local.length == 9 && local[0] == '9') || (local.length == 8)

            if (!validLocal) return null

            return Phone("+$digits")
        }

        private fun isValidE164(e164: String): Boolean {
            if (!e164.startsWith("+55")) return false
            val d = e164.substring(1)
            return fromDigitsOrNull(d) != null
        }

        private fun onlyDigits(input: CharSequence?): String? {
            if (input.isNullOrBlank()) return null
            val buf = CharArray(input.length)
            var p = 0
            for (c in input) if (c in '0'..'9') buf[p++] = c
            if (p == 0) return null
            return String(buf, 0, p)
        }

        private val DDD_TABLE = setOf(
            "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "21", "22", "24", "27", "28",
            "31", "32", "33", "34", "35", "37", "38",
            "41", "42", "43", "44", "45", "46", "47", "48", "49",
            "51", "53", "54", "55",
            "61", "62", "63", "64", "65", "66", "67", "68", "69",
            "71", "73", "74", "75", "77", "79",
            "81", "82", "83", "84", "85", "86", "87", "88", "89",
            "91", "92", "93", "94", "95", "96", "97", "98", "99"
        )
    }
}