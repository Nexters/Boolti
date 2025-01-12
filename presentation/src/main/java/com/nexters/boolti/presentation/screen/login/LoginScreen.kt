package com.nexters.boolti.presentation.screen.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtCloseableAppBar
import com.nexters.boolti.presentation.component.KakaoLoginButton
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val snackbarController = LocalSnackbarController.current

    val sheetState = rememberModalBottomSheetState()
    var showSignOutCancelledDialog by remember { mutableStateOf(false) }
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    val loginFailedMessage = stringResource(id = R.string.login_failed)
    val signupFailedMessage = stringResource(id = R.string.signup_failed)

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                LoginEvent.Success -> onBackPressed()
                LoginEvent.RequireSignUp -> isSheetOpen = true
                LoginEvent.SignOutCancelled -> showSignOutCancelledDialog = true
                LoginEvent.Invalid -> snackbarController.showMessage(loginFailedMessage)
                LoginEvent.SignupFailed -> snackbarController.showMessage(signupFailedMessage)
            }
        }
    }

    BackHandler(onBack = onBackPressed)

    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                isSheetOpen = false
            },
            dragHandle = {},
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {
            SignUpBottomSheet(
                signUp = viewModel::signUp,
            )
        }
    }

    if (showSignOutCancelledDialog) {
        SignOutCancelledDialog {
            showSignOutCancelledDialog = false
            onBackPressed()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { BtCloseableAppBar(onClickClose = onBackPressed) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = marginHorizontal),
                    text = stringResource(id = R.string.catch_phrase),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
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
private fun SignUpBottomSheet(
    signUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    val term = stringResource(id = R.string.term_notice)
    val tag = stringResource(id = R.string.term_notice_tag)
    val spanOffset = Pair(term.indexOf(tag), tag.length)

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .navigationBarsPadding(),
    ) {
        Text(
            modifier = Modifier
                .padding(top = 24.dp, bottom = 12.dp)
                .height(32.dp),
            text = stringResource(id = R.string.signup_greeting),
            style = MaterialTheme.typography.headlineSmall
        )

        ClickableText(
            text = buildAnnotatedString {
                append(term)
                val (start, length) = spanOffset
                addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline),
                    start,
                    start + length,
                )
            },
            onClick = { offset ->
                val (start, length) = spanOffset
                if (start <= offset && offset < (start + length)) {
                    uriHandler.openUri("https://boolti.notion.site/b4c5beac61c2480886da75a1f3afb982")
                }
            },
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        )
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .padding(vertical = 8.dp)
                .padding(bottom = 20.dp),
            label = stringResource(id = R.string.signup_with_agreement),
            onClick = signUp,
        )
    }
}

@Composable
fun SignOutCancelledDialog(onDismiss: () -> Unit) {
    BTDialog(
        onDismiss = onDismiss,
        onClickPositiveButton = onDismiss,
    ) {
        Text(
            text = stringResource(R.string.signout_cancelled_message),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
