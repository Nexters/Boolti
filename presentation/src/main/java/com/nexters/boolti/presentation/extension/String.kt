package com.nexters.boolti.presentation.extension

import android.content.Context
import com.nexters.boolti.presentation.R
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

/**
 * ## 카드사 코드로 변환
 *
 * 해당 코드는 토스페이먼츠에서 정의한 코드이므로 결제 SDK 가 변경되면 유효하지 않음.
 *
 * [토스페이먼츠 문서](https://docs.tosspayments.com/reference/codes#%EC%B9%B4%EB%93%9C%EC%82%AC-%EC%BD%94%EB%93%9C) 참고
 */
fun String.cardCodeToCompanyName(context: Context): String = when (this) {
    "3K" -> R.string.card_ibk_bc
    "46" -> R.string.card_gwangjubank
    "71" -> R.string.card_lotte
    "30" -> R.string.card_kdbbank
    "31" -> R.string.card_bc
    "51" -> R.string.card_samsung
    "38" -> R.string.card_saemaul
    "41" -> R.string.card_shinhan
    "62" -> R.string.card_shinhyeop
    "36" -> R.string.card_citi
    "33" -> R.string.card_woori_bc
    "W1" -> R.string.card_woori
    "37" -> R.string.card_post
    "39" -> R.string.card_savingbank
    "35" -> R.string.card_jeonbukbank
    "42" -> R.string.card_jejubank
    "15" -> R.string.card_kakaobank
    "3A" -> R.string.card_kbank
    "24" -> R.string.card_tossbank
    "21" -> R.string.card_hana
    "61" -> R.string.card_hyundai
    "11" -> R.string.card_kookmin
    "91" -> R.string.card_nonghyeop
    "34" -> R.string.card_suhyeop
    else -> R.string.blank
}.run { context.getString(this) }
