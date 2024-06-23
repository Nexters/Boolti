package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GiftCompleteScreen(
    popBackStack: () -> Unit,
) {
    Scaffold {
        Text(text = "선물 완료")
    }
}