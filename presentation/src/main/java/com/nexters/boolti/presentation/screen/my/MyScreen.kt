package com.nexters.boolti.presentation.screen.my

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
    requireLogin: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column {
            Button(onClick = requireLogin) {
                Text(text = "로그인 화면으로", style = MaterialTheme.typography.headlineMedium)
            }
            Button(
                modifier = Modifier.padding(top = 20.dp),
                onClick = viewModel::logout
            ) {
                Text(text = "로그아웃", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
