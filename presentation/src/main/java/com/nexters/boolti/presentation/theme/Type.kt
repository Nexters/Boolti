package com.nexters.boolti.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
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
    Font(R.font.sb_aggro_m, FontWeight.Normal),
)

private val headline3 = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 28.sp,
    lineHeight = 40.sp,
)

private val headline2 = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
)

private val headline1 = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
)

private val subhead2 = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 26.sp,
)

private val subhead1 = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)

private val body4 = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 26.sp,
)

private val body3 = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
)

private val body2 = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 23.sp,
)

private val body1 = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 22.sp,
)

private val caption = createTextStyle(
    fontFamily = pretendardFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 18.sp,
)

val point1 = createTextStyle(
    fontFamily = aggroFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 26.sp,
)

val point2 = createTextStyle(
    fontFamily = aggroFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 30.sp,
)

val point4 = createTextStyle(
    fontFamily = aggroFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 34.sp,
)

private fun createTextStyle(
    fontSize: TextUnit,
    lineHeight: TextUnit,
    fontFamily: FontFamily = pretendardFamily,
    fontWeight: FontWeight = FontWeight.Normal,
): TextStyle {
    return TextStyle(
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = lineHeight,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None
        ),
    )
}

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