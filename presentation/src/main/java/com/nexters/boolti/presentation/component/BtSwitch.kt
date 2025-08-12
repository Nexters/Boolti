package com.nexters.boolti.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80

@Composable
fun BtSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    colors: SwitchColors = SwitchDefaults.colors(
        uncheckedThumbColor = Grey50,
        uncheckedTrackColor = Grey80,
    ),
    thumbContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
        )
    },
) {
    Switch(
        modifier = modifier,
        checked = checked,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        colors = colors,
        thumbContent = thumbContent,
    )
}
