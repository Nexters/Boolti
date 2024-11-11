package com.nexters.boolti.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.nexters.boolti.presentation.R
import java.time.Duration
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
 * ex) 2024.01.20 (토) 18:00
 */
val LocalDateTime.showDateTimeString: String
    @Composable
    get() = run {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd ($dayOfWeekString) HH:mm")
        format(formatter)
    }

fun LocalDateTime.format(pattern: String): String = format(DateTimeFormatter.ofPattern(pattern))

val LocalDateTime.dDay: Long
    get() = run {
        val today = LocalDate.now()
        ChronoUnit.DAYS.between(today, toLocalDate())
    }

val LocalDateTime.countDownString: String
    @Composable
    get() = run {
        val now = LocalDateTime.now()

        val duration = Duration.between(now, this)

        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60

        stringResource(id = R.string.ticketing_button_ticket_countdown, days) +
                " ${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
    }