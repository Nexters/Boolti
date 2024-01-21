package com.nexters.boolti.presentation.screen.my

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MyScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel()) {
    Box(
        modifier = modifier.padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Button(onClick = { viewModel.logout() }) {
            Text(text = "로그아웃", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
