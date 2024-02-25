package com.nexters.boolti.presentation.extension

import java.lang.StringBuilder

fun String.filterToPhoneNumber(): String = filter { it.isDigit() }.run {
    substring(0..minOf(10, lastIndex))
}

fun String.sliceAtMost(maxLength: Int): String = slice(0 until minOf(maxLength, length))

fun String.toContactFormat(sep: Char = '-'): String = StringBuilder().apply {
    filterToPhoneNumber().forEachIndexed { i, n ->
        if (i in listOf(3, 7)) append(sep)
        append(n)
    }
}.toString()
