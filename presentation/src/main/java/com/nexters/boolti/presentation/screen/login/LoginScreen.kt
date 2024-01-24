package com.nexters.boolti.presentation.screen.login

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.component.KakaoLoginButton
import com.nexters.boolti.presentation.icons.IconPack
import com.nexters.boolti.presentation.icons.iconpack.ArrowBack

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel.event) {
        viewModel.event.collect {
            when (it) {
                LoginEvent.Success -> Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                LoginEvent.RequireSignUp -> Toast.makeText(context, "회원가입 필요", Toast.LENGTH_SHORT)
                    .show()

                LoginEvent.Invalid -> Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    BackHandler(onBack = onBackPressed)

    Scaffold(
        topBar = { LoginAppBar(previousScreen = "이전 화면") },
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                KakaoLoginButton()
            }
        }
    }
}

@Composable
private fun LoginAppBar(
    modifier: Modifier = Modifier,
    previousScreen: String,
) {
    Box(
        modifier = modifier.height(44.dp),
    ) {
        Row(
            modifier = Modifier.padding(end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = IconPack.ArrowBack,
                    contentDescription = "뒤로가기",
                    modifier
                        .padding(start = 20.dp, top = 10.dp, end = 4.dp, bottom = 10.dp)
                        .size(width = 24.dp, height = 24.dp)
                )
            }
            Text(previousScreen, style = MaterialTheme.typography.titleMedium)
        }
    }
}