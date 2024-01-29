package com.nexters.boolti.presentation.extension

fun String.filterToPhoneNumber(): String = filter { it.isDigit() }.run {
    substring(0..minOf(10, lastIndex))
}
