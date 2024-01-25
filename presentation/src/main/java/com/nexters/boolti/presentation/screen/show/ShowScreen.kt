package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ShowScreen(
    modifier: Modifier = Modifier,
    onClickTicketing: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Button(onClick = onClickTicketing) {
            Text(text = "예매하기", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
