package com.nexters.boolti.presentation.screen.giftprequestion

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GiftPreQuestionScreen(
    giftUuid: String,
    showId: String,
    onBackPressed: () -> Unit,
) {
    Text("선물 사전 질문 작성")
}