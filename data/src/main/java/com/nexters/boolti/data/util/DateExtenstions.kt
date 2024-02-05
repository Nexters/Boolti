package com.nexters.boolti.data.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

internal fun String.toLocalDate(): LocalDate = this.toLocalDateTime().toLocalDate()

internal fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this.format(formatter))
