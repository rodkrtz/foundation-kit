package com.rodkrtz.foundationkit.valueobject

import java.math.RoundingMode

public fun Double.round(decimals: Int): Double {
    return this.toBigDecimal().setScale(decimals, RoundingMode.CEILING).toDouble()
}