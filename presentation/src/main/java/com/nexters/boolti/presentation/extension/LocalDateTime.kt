package com.nexters.boolti.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.nexters.boolti.presentation.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ## 요일
 */
val LocalDateTime.dayOfWeekString: String
    @Composable
    get() = run {
        val dayOfWeekArr = stringArrayResource(id = R.array.days_of_week)
        dayOfWeekArr[dayOfWeek.value - 1]
    }

/**
 * ## yyyy.MM.dd (A) HH:mm
 *
 * ex) 2024.01.20 (토) / 18:00
 */
val LocalDateTime.showDateTimeString: String
    @Composable
    get() = run {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd ($dayOfWeekString) / HH:mm")
        format(formatter)
    }

/**
 * ## yyyy.MM.dd (A)
 *
 * ex) 2024.01.20 (토)
 */
val LocalDateTime.showDateString: String
    @Composable
    get() = run {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd ($dayOfWeekString)")
        format(formatter)
    }

fun LocalDateTime.format(pattern: String): String = format(DateTimeFormatter.ofPattern(pattern))
