package com.nexters.boolti.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

@Composable
fun CircleBgIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int,
    bgColor: Color,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
    ) {
        Icon(imageVector = ImageVector.vectorResource(iconId), contentDescription = null)
    }
}
