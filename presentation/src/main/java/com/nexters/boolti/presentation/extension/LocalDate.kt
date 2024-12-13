package com.nexters.boolti.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.nexters.boolti.presentation.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ## 요일
 */
val LocalDate.dayOfWeekString: String
    @Composable
    get() = run {
        val dayOfWeekArr = stringArrayResource(id = R.array.days_of_week)
        dayOfWeekArr[dayOfWeek.value - 1]
    }

/**
 * ## yyyy.MM.dd (A)
 *
 * ex) 2024.01.20 (토)
 */
val LocalDate.showDateString: String
    @Composable
    get() = run {
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd ($dayOfWeekString)")
        format(formatter)
    }

fun LocalDate.format(pattern: String): String = format(DateTimeFormatter.ofPattern(pattern))
