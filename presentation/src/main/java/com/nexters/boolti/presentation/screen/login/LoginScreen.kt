package com.nexters.boolti.presentation.screen.login

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.R
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
        topBar = { LoginAppBar(previousScreen = "이전 화면", onBackPressed = onBackPressed) },
    ) { innerPadding ->
        Box(
            modifier = modifier.padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(id = R.string.catch_phrase),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    stringResource(id = R.string.catch_phrase_sub),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 6.dp)
                )

                val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
                KakaoLoginButton(
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .padding(horizontal = marginHorizontal)
                )
            }
        }
    }
}

@Composable
private fun LoginAppBar(
    modifier: Modifier = Modifier,
    previousScreen: String,
    onBackPressed: () -> Unit,
) {
    Box(
        modifier = modifier.height(44.dp),
    ) {
        val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
        Row(
            modifier = Modifier.padding(end = marginHorizontal),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = IconPack.ArrowBack,
                    contentDescription = "뒤로가기",
                    modifier
                        .padding(start = marginHorizontal, top = 10.dp, end = 4.dp, bottom = 10.dp)
                        .size(width = 24.dp, height = 24.dp)
                )
            }
            Text(previousScreen, style = MaterialTheme.typography.titleMedium)
        }
    }
}