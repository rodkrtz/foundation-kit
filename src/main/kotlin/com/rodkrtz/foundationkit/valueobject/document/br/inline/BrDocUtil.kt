package com.rodkrtz.foundationkit.valueobject.document.br.inline

public object BrDocUtil {

    public fun onlyDigits(input: CharSequence?): String {
        if (input.isNullOrEmpty()) return ""

        val len = input.length
        val buffer = CharArray(len)
        var count = 0

        for (i in 0 until len) {
            val c = input[i]
            if (c in '0'..'9') {
                buffer[count++] = c
            }
        }

        return if (count == len && input is String) {
            input
        } else {
            String(buffer, 0, count)
        }
    }

    public fun extractDigits(input: String, expected: Int): String {
        val buf = CharArray(expected)
        var pos = 0
        for (c in input) {
            if (c in '0'..'9') {
                if (pos == expected) return ""
                buf[pos++] = c
            }
        }
        if (pos != expected) return ""
        return String(buf)
    }

    public fun extractBase36(input: String, expected: Int): String {
        val buf = CharArray(expected)
        var pos = 0
        for (c0 in input) {
            val c = c0.uppercaseChar()
            if ((c in '0'..'9') || (c in 'A'..'Z')) {
                if (pos == expected) return ""
                buf[pos++] = c
            }
        }
        if (pos != expected) return ""
        return String(buf)
    }

    public fun isRepeated(chars: CharArray): Boolean {
        if (chars.isEmpty()) return false
        val f = chars[0]
        for (i in 1 until chars.size) if (chars[i] != f) return false
        return true
    }

    public fun base36Value(c: Char): Int {
        return when (c) {
            in '0'..'9' -> c - '0'
            in 'A'..'Z' -> (c - 'A') + 10
            else -> throw IllegalArgumentException("Invalid base36 char: $c")
        }
    }
}