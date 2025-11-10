package com.nexters.boolti.presentation.component

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.theme.Grey70

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BtBottomSheet(
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val view = LocalView.current
    val navigationBarColor = MaterialTheme.colorScheme.surfaceTint

    DisposableEffect(Unit) {
        val window = (view.context as Activity).window
        val originalNavBarColor = window.navigationBarColor

        window.navigationBarColor = navigationBarColor.toArgb()

        onDispose {
            window.navigationBarColor = originalNavBarColor
        }
    }

    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.surfaceTint,
        onDismissRequest = onDismissRequest,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 24.dp)
                    .size(45.dp, 4.dp)
                    .clip(CircleShape)
                    .background(color = Grey70)
            )
        },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        content()
        Spacer(Modifier.height(28.dp))
    }
}
