package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import com.nexters.boolti.presentation.theme.Grey50

@Composable
fun TicketDetailScreen(
    modifier: Modifier,
) {
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "입장 코드 입력하기",
            style = MaterialTheme.typography.bodySmall,
            color = Grey50,
            textDecoration = TextDecoration.Underline,
        )
    }
}
