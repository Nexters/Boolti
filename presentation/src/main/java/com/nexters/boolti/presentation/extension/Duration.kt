package com.nexters.boolti.presentation.extension

import java.time.Duration

fun Duration.asString(): String {
    val hours = toHours() % 24
    val minutes = toMinutes() % 60
    val seconds = seconds % 60

    return "${hours.toString().padStart(2, '0')}:" +
            "${minutes.toString().padStart(2, '0')}:" +
            seconds.toString().padStart(2, '0')
}

val Duration.dDay: Long
    get() = toDays() + 1