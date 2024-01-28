package com.nexters.boolti.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.nexters.boolti.presentation.R

@Composable
fun getMarginHorizontal(): Dp {
    return dimensionResource(id = R.dimen.margin_horizontal)
}
