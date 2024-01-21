package com.nexters.boolti.presentation.screen.my

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel()) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Button(onClick = { viewModel.logout() }) {
            Text(text = "로그아웃", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
