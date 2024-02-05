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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.KakaoLoginButton
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                LoginEvent.Success -> {
                    onBackPressed()
                }

                LoginEvent.RequireSignUp -> {
                    isSheetOpen = true
                }

                LoginEvent.Invalid -> Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    BackHandler(onBack = onBackPressed)

    if (isSheetOpen) {
        ModalBottomSheet(sheetState = sheetState, onDismissRequest = {
            isSheetOpen = false
        }) {
            SignUpBottomSheet(
                signUp = viewModel::signUp,
            )
        }
    }

    Scaffold(
        topBar = { LoginAppBar(onBackPressed = onBackPressed) },
        containerColor = MaterialTheme.colorScheme.background,
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
        IconButton(
            modifier = Modifier.size(width = 48.dp, height = 44.dp), onClick = onBackPressed
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.description_navigate_back),
                modifier
                    .padding(start = marginHorizontal)
                    .size(width = 24.dp, height = 24.dp)
            )
        }
    }
}

@Composable
private fun SignUpBottomSheet(
    signUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
    ) {
        Text(
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            text = stringResource(id = R.string.signup_greeting),
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