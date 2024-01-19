package com.nexters.boolti.presentation.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nexters.boolti.presentation.component.KakaoLoginButton

@Composable
fun HomeScreen() {
    Column(Modifier.fillMaxSize()) {
        KakaoLoginButton()
    }
}
