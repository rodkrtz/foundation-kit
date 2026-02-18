package com.rodkrtz.foundationkit.valueobject.person

import java.time.Clock
import java.time.LocalDate
import java.time.Period

@JvmInline
public value class BirthDate(public val value: LocalDate) {

    init {
        val today = LocalDate.now(Clock.systemUTC())

        require(!value.isAfter(today)) {
            "Birth date cannot be in the future"
        }

        require(Period.between(value, today).years <= 120) {
            "Invalid birth date: age > 120"
        }
    }

    public fun age(clock: Clock = Clock.systemUTC()): Int =
        Period.between(value, LocalDate.now(clock)).years

    public fun isAdult(clock: Clock = Clock.systemUTC(), minAge: Int = 18): Boolean =
        age(clock) >= minAge

    public fun masked(): String = "${value.year}-**-**"

    override fun toString(): String = value.toString()

    public companion object {

        public fun parse(iso: String): BirthDate =
            BirthDate(LocalDate.parse(iso))

        public fun of(year: Int, month: Int, day: Int): BirthDate =
            BirthDate(LocalDate.of(year, month, day))
    }
}