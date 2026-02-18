package com.rodkrtz.foundationkit.valueobject.document.br

import com.rodkrtz.foundationkit.valueobject.document.br.inline.BrDocUtil
import kotlin.random.Random

@JvmInline
public value class CNPJ private constructor(public val value: String) {

    public enum class Format { MASKED, DIGITS, SAFE }

    public fun format(style: Format = Format.DIGITS): String = when (style) {
        Format.DIGITS -> value
        Format.MASKED -> {
            buildString(18) {
                append(value, 0, 2).append('.')
                append(value, 2, 5).append('.')
                append(value, 5, 8).append('/')
                append(value, 8, 12).append('-')
                append(value, 12, 14)
            }
        }
        Format.SAFE -> {
            buildString(18) {
                append("**.")
                append(value, 2, 5).append('.')
                append(value, 5, 8).append('/')
                append(value, 8, 12)
                append("-**")
            }
        }
    }

    public val digitsOnly: String get() = value
    public val base12: String get() = value.substring(0, 12)
    public val verifierDigits: String get() = value.substring(12, 14)
    override fun toString(): String = value

    public companion object {

        // Pesos oficiais CNPJ
        private val WEIGHT1 = intArrayOf(5,4,3,2,9,8,7,6,5,4,3,2)
        private val WEIGHT2 = intArrayOf(6,5,4,3,2,9,8,7,6,5,4,3,2)

        public fun parse(input: String): CNPJ = tryParse(input).getOrThrow()

        public fun tryParse(input: String): Result<CNPJ> {
            val base12 = BrDocUtil.extractBase36(input, 12)

            if (base12.isEmpty()) {
                return Result.failure(IllegalArgumentException("Invalid CNPJ: $input"))
            }

            val dv = BrDocUtil.extractDigits(input.takeLast(4), 2)

            if (dv.length != 2) {
                return Result.failure(IllegalArgumentException("Invalid CNPJ: $input"))
            }
            val normalized = base12 + dv

            if (!isValidNormalized(normalized)) {
                return Result.failure(IllegalArgumentException("Invalid CNPJ: $input"))
            }
            return Result.success(CNPJ(normalized))
        }

        public fun isValid(input: String): Boolean =
            tryParse(input).isSuccess

        public fun fromBase(base12: String): CNPJ {
            require(base12.length == 12 && base12.all { it.isDigit() || it.isUpperCase() }) {
                "The base must contain 12 characters [0-9A-Z]"
            }
            val d1 = calcDigit(base12, WEIGHT1)
            val d2 = calcDigit(base12 + d1, WEIGHT2)
            return CNPJ("$base12$d1$d2")
        }

        public fun random(alphaNumeric: Boolean = true): CNPJ {
            val base = CharArray(12)
            do {
                for (i in 0 until 12) {
                    base[i] = if (alphaNumeric)
                        (if (Random.nextBoolean()) ('0' + Random.nextInt(10)) else ('A' + Random.nextInt(26)))
                    else
                        ('0' + Random.nextInt(10))
                }
            } while (BrDocUtil.isRepeated(base))

            val b = String(base)
            val d1 = calcDigit(b, WEIGHT1)
            val d2 = calcDigit(b + d1, WEIGHT2)
            return CNPJ("$b$d1$d2")
        }

        public fun randomList(count: Int, alphaNumeric: Boolean = true): List<CNPJ> {
            require(count >= 0)
            return List(count) { random(alphaNumeric) }
        }

        private fun isValidNormalized(n: String): Boolean {
            if (n.length != 14) return false

            val f = n[0]
            var rep = true
            for (i in 1 until 12) if (n[i] != f) { rep = false; break }
            if (rep) return false

            val d1 = calcDigit(n.substring(0,12), WEIGHT1)
            if (n[12] != ('0' + d1)) return false

            val d2 = calcDigit(n.substring(0,12) + d1, WEIGHT2)
            return n[13] == ('0' + d2)
        }

        private fun calcDigit(base: String, weights: IntArray): Int {
            var sum = 0
            for (i in base.indices) {
                val v = BrDocUtil.base36Value(base[i])
                sum += v * weights[i]
            }
            val r = sum % 11
            return if (r < 2) 0 else 11 - r
        }
    }
}