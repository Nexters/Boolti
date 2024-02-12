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

fun LocalDateTime.format(pattern: String): String = format(DateTimeFormatter.ofPattern(pattern))
