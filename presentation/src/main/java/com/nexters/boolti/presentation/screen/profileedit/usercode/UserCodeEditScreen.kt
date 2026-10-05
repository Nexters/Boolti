package com.nexters.boolti.presentation.screen.profileedit.usercode

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
fun UserCodeEditScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserCodeEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            UserCodeEditEvent.NavigateUp -> navigateUp()
        }
    }

    BackHandler { viewModel.onAction(UserCodeEditAction.ClickBack) }

    UserCodeEditScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun UserCodeEditScreen(
    uiState: UserCodeEditUiState,
    onAction: (UserCodeEditAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = { onAction(UserCodeEditAction.ClickBack) },
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                title = stringResource(R.string.label_id),
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.save_short),
                        onClick = { onAction(UserCodeEditAction.Save) },
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
                text = uiState.userCode,
                onValueChanged = { onAction(UserCodeEditAction.ChangeUserCode(it)) },
                isError = uiState.userCodeError != null,
                supportingText = uiState.userCodeError?.let {
                    when (it) {
                        UserCodeError.MinLength -> stringResource(
                            R.string.validate_min_length,
                            4,
                        )

                        UserCodeError.MaxLength -> stringResource(
                            R.string.input_upper_limit_text,
                            20,
                        )

                        UserCodeError.ContainsWhitespace -> stringResource(R.string.validate_whitespace)

                        UserCodeError.Invalid -> stringResource(R.string.validate_edit_usercode)
                        UserCodeError.Duplicated -> stringResource(R.string.validate_duplicated_usercode)
                    }
                },
                placeholder = stringResource(R.string.nickname_edit_placeholder),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(horizontal = marginHorizontal),
                text = stringResource(R.string.usercode_edit_description),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
        }

        if (uiState.showExitAlertDialog) {
            BTDialog(
                enableDismiss = true,
                showCloseButton = true,
                onDismiss = { onAction(UserCodeEditAction.DismissExitAlertDialog) },
                negativeButtonLabel = stringResource(R.string.btn_exit),
                onClickNegativeButton = { onAction(UserCodeEditAction.ConfirmExit) },
                positiveButtonLabel = stringResource(R.string.save),
                onClickPositiveButton = { onAction(UserCodeEditAction.Save) },
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
private fun UserCodeEditScreenPreview() {
    BooltiTheme {
        UserCodeEditScreen(
            uiState = UserCodeEditUiState(userCode = "a1234"),
            onAction = {},
        )
    }
}
