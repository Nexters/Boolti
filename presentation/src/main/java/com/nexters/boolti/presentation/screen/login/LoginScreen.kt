package com.nexters.boolti.presentation.screen.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel.event) {
        viewModel.event.collect {
            when (it) {
                LoginEvent.Success -> Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                LoginEvent.RequireSignUp -> Toast.makeText(context, "회원가입 필요", Toast.LENGTH_SHORT).show()
                LoginEvent.Invalid -> Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        contentAlignment = Alignment.Center,
    ) {
        Button(onClick = { viewModel.login() }) {
            Text(text = "로그인")
        }
    }
}
