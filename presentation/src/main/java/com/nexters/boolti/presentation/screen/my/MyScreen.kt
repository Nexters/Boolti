package com.nexters.boolti.presentation.screen.my

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun MyScreen(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
    requireLogin: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Row {
//            AsyncImage(model = "", contentDescription = null)
            Column {
                Text(text = "김불티")
                Text(text = "boolti1234@gmail.com")
            }
        }

        MyButton(text = "예매 내역", onClick = {})
        MyButton(text = "QR 스캔", onClick = {})

        Text(
            modifier = Modifier.clickable(onClick = viewModel::logout),
            text = "로그아웃", style = MaterialTheme.typography.headlineMedium
        )
    }
}

// todo : 적절한 이름이 없을까...?
@Composable
private fun MyButton(text: String, onClick: () -> Unit) {

}