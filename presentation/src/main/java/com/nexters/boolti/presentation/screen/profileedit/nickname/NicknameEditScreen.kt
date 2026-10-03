package com.nexters.boolti.presentation.screen.profileedit.nickname

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTClearableTextField
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents

@Composable
fun NicknameEditScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NicknameEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            NicknameEditEvent.NavigateUp -> navigateUp()
        }
    }

    BackHandler { viewModel.onAction(NicknameEditAction.ClickBack) }

    NicknameEditScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun NicknameEditScreen(
    uiState: NicknameEditUiState,
    onAction: (NicknameEditAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = { onAction(NicknameEditAction.ClickBack) },
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                title = stringResource(R.string.label_nickname),
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.save_short),
                        onClick = { onAction(NicknameEditAction.Save) },
                        enabled = uiState.saveEnabled,
                    )
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BTClearableTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .padding(horizontal = marginHorizontal),
                text = uiState.nickname,
                onValueChanged = { onAction(NicknameEditAction.ChangeNickname(it)) },
                isError = uiState.nicknameError != null,
                supportingText = uiState.nicknameError?.let {
                    when (it) {
                        NicknameError.MinLength -> stringResource(
                            R.string.validate_min_length,
                            1,
                        )

                        NicknameError.MaxLength -> stringResource(
                            R.string.input_upper_limit_text,
                            12,
                        )

                        NicknameError.NotTrimmed -> stringResource(R.string.validate_trimmed)

                        NicknameError.Invalid -> stringResource(R.string.validate_edit_nickname)
                    }
                },
                placeholder = stringResource(R.string.nickname_edit_placeholder),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(horizontal = marginHorizontal),
                text = stringResource(R.string.nickname_edit_description),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
        }

        if (uiState.showExitAlertDialog) {
            BTDialog(
                enableDismiss = true,
                showCloseButton = true,
                onDismiss = { onAction(NicknameEditAction.DismissExitAlertDialog) },
                negativeButtonLabel = stringResource(R.string.btn_exit),
                onClickNegativeButton = { onAction(NicknameEditAction.ConfirmExit) },
                positiveButtonLabel = stringResource(R.string.save),
                onClickPositiveButton = { onAction(NicknameEditAction.Save) },
            ) {
                Text(
                    text = stringResource(R.string.profile_edit_exit_alert),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun NicknameEditScreenPreview() {
    BooltiTheme {
        NicknameEditScreen(
            uiState = NicknameEditUiState(nickname = "mangbaam"),
            onAction = {},
        )
    }
}
