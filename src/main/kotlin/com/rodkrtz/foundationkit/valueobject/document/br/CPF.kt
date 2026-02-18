package com.rodkrtz.foundationkit.valueobject.document.br

import com.rodkrtz.foundationkit.valueobject.document.br.inline.BrDocUtil
import kotlin.random.Random

@JvmInline
public value class CPF private constructor(public val value: String) {

    public enum class Format { MASKED, DIGITS, SAFE }

    public val digitsOnly: String get() = value

    public val baseDigits: String get() = value.substring(0, 9)

    public val verifierDigits: String get() = value.substring(9, 11)

    public fun format(style: Format = Format.DIGITS): String = when (style) {
        Format.DIGITS -> value
        Format.MASKED -> buildString(14) {
            append(value, 0, 3).append('.')
            append(value, 3, 6).append('.')
            append(value, 6, 9).append('-')
            append(value, 9, 11)
        }
        Format.SAFE -> buildString(14) {
            append("***.")
            append(value, 3, 6).append('.')
            append(value, 6, 9)
            append("-**")
        }
    }

    public fun asLong(): Long = value.toLong()
    override fun toString(): String = value

    public companion object {

        public fun parse(input: String): CPF = tryParse(input).getOrThrow()

        public fun tryParse(input: String): Result<CPF> {
            val digits = BrDocUtil.extractDigits(input, 11)
            if (!isValidDigits(digits)) {
                return Result.failure(IllegalArgumentException("Invalid CPF: $input"))
            }
            return Result.success(CPF(digits))
        }

        public fun isValid(input: String): Boolean =
            isValidDigits(BrDocUtil.extractDigits(input, 11))

        public fun fromBase(base9: String): CPF {
            require(base9.length == 9 && base9.all { it.isDigit() }) {
                "The base must contain 9 numeric digits"
            }
            val d1 = calcDigit(base9, 9, 10)
            val d2 = calcDigit(base9 + d1, 10, 11)
            return CPF("$base9$d1$d2")
        }

        public fun random(): CPF {
            val base = CharArray(9)
            do {
                for (i in 0 until 9) base[i] = ('0' + Random.nextInt(10))
            } while (BrDocUtil.isRepeated(base))
            val b = String(base)
            val d1 = calcDigit(b, 9, 10)
            val d2 = calcDigit(b + d1, 10, 11)
            return CPF("$b$d1$d2")
        }

        public fun randomList(count: Int): List<CPF> {
            require(count >= 0)
            return List(count) { random() }
        }

        private fun isValidDigits(d: String): Boolean {
            if (d.length != 11)
                return false

            val f = d[0]
            var rep = true

            for (i in 1 until 11) if (d[i] != f) { rep = false; break }
            if (rep) return false
            val d1 = calcDigit(d, 9, 10)
            if (d[9] != ('0' + d1)) return false
            val d2 = calcDigit(d, 10, 11)
            return d[10] == ('0' + d2)
        }

        private fun calcDigit(d: String, len: Int, wStart: Int): Int {
            var sum = 0
            var w = wStart
            for (i in 0 until len) sum += (d[i] - '0') * w--
            val r = sum % 11
            return if (r < 2) 0 else 11 - r
        }
    }
}