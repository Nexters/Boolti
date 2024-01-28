package com.nexters.boolti.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nexters.boolti.presentation.R

/**
 * Figma 디자인의 Design system을 참고하세요.
 */

val pretendardFamily = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_semi_bold, FontWeight.SemiBold)
)

val aggroFamily = FontFamily(
    Font(R.font.sb_aggro_b, FontWeight.Normal),
)

private val headline3 = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    lineHeight = 40.sp,
)
private val headline2 = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
)
private val headline1 = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
)

private val subhead2 = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 26.sp,
)
private val subhead1 = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)

private val body4 = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 26.sp,
)
private val body3 = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)
private val body2 = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 23.sp,
)
private val body1 = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 22.sp,
)

private val caption = TextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 18.sp,
)

val Typography = Typography(
    headlineLarge = headline3,
    headlineMedium = headline2,
    headlineSmall = headline1,

    titleLarge = subhead2,
    titleMedium = subhead1,

    titleSmall = body4,
    bodyLarge = body3,
    bodyMedium = body2,
    bodySmall = body1,

    labelMedium = caption,
)