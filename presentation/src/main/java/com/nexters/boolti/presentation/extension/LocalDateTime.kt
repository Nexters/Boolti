package com.nexters.boolti.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.nexters.boolti.presentation.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

fun LocalDateTime.format(pattern: String): String = format(DateTimeFormatter.ofPattern(pattern))
