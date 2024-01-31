package com.nexters.boolti.presentation.screen.login

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.KakaoLoginButton
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey95
import com.nexters.boolti.presentation.theme.marginHorizontal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                LoginEvent.Success -> onBackPressed()
                LoginEvent.RequireSignUp -> {
                    scaffoldState.bottomSheetState.expand()
                }

                LoginEvent.Invalid -> Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    BackHandler(onBack = onBackPressed)

    BottomSheetScaffold(
        topBar = { LoginAppBar(onBackPressed = onBackPressed) },
        sheetContent = {
            SignUpBottomSheet(
                nickname = uiState.nickname ?: stringResource(R.string.nickname_default),
                signUp = viewModel::signUp
            )
        },
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

                KakaoLoginButton(
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .padding(horizontal = marginHorizontal),
                    onClick = viewModel::loginWithKaKao,
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
            .fillMaxWidth()
            .height(44.dp)
            .background(color = MaterialTheme.colorScheme.background),
    ) {
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
private fun SignUpBottomSheet(
    nickname: String,
    signUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
    ) {
        Text(
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            text = stringResource(id = R.string.signup_greeting, nickname),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            modifier = Modifier.height(66.dp),
            text = stringResource(id = R.string.term_notice),
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        )
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(bottom = 34.dp),
            label = stringResource(id = R.string.signup_with_agreement),
            onClick = signUp,
        )
    }
}