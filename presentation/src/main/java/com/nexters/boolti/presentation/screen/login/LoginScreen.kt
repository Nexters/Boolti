package com.nexters.boolti.presentation.screen.login

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.CtaButton
import com.nexters.boolti.presentation.component.KakaoLoginButton
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey95

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                LoginEvent.Success -> Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                LoginEvent.RequireSignUp -> {
                    Toast.makeText(context, "회원가입 필요", Toast.LENGTH_SHORT)
                        .show()
                    scaffoldState.bottomSheetState.expand()
                }

                LoginEvent.Invalid -> Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    BackHandler(onBack = onBackPressed)

    BottomSheetScaffold(
        topBar = { LoginAppBar(onBackPressed = onBackPressed) },
        sheetContent = { SignUpBottomSheet() },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContainerColor = Grey85,
        containerColor = Grey95,
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
                        .padding(horizontal = marginHorizontal),
                    onClick = viewModel::login,
                )
            }
        }
    }
}

@Composable
private fun LoginAppBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(color = Grey95),
    ) {
        val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
        IconButton(onClick = onBackPressed) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "뒤로가기",
                modifier
                    .padding(start = marginHorizontal, top = 10.dp, end = 4.dp, bottom = 10.dp)
                    .size(width = 24.dp, height = 24.dp)
            )
        }
    }
}

@Composable
private fun SignUpBottomSheet(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
    ) {
        Text(
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            text = "어서오세요 %닉네임 최대 10자%님!",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            modifier = Modifier.height(66.dp),
            text = stringResource(id = R.string.term_notice),
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        )
        CtaButton(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(bottom = 34.dp),
            text = stringResource(id = R.string.signup_with_agreement),
            onClick = {},
        )
    }
}