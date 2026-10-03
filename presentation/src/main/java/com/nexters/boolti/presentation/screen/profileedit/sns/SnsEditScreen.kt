package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTClearableTextField
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.FixedWidthText
import com.nexters.boolti.presentation.extension.centerToTop
import com.nexters.boolti.presentation.extension.icon
import com.nexters.boolti.presentation.extension.label
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents

@Composable
fun SnsEditScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SnsEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            SnsEditEvent.NavigateUp -> navigateUp()
        }
    }

    BackHandler { viewModel.onAction(SnsEditAction.ClickBack) }

    SnsEditScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun SnsEditScreen(
    uiState: SnsEditUiState,
    onAction: (SnsEditAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                title = stringResource(R.string.sns),
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = { onAction(SnsEditAction.ClickBack) },
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.save_short),
                        onClick = { onAction(SnsEditAction.Save) },
                        enabled = uiState.saveEnabled,
                    )
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SnsUsernameInput(
                snsType = Sns.SnsType.INSTAGRAM,
                username = uiState.instagramUsername,
                onUsernameChanged = { onAction(SnsEditAction.ChangeInstagramUsername(it)) },
                error = uiState.instagramUsernameError,
            )
            SnsUsernameInput(
                snsType = Sns.SnsType.YOUTUBE,
                username = uiState.youtubeUsername,
                onUsernameChanged = { onAction(SnsEditAction.ChangeYoutubeUsername(it)) },
                error = uiState.youtubeUsernameError,
            )
        }

        if (uiState.showExitAlertDialog) {
            BTDialog(
                enableDismiss = true,
                showCloseButton = true,
                onDismiss = { onAction(SnsEditAction.ConfirmExit) },
                negativeButtonLabel = stringResource(R.string.btn_exit),
                onClickNegativeButton = { onAction(SnsEditAction.ConfirmExit) },
                positiveButtonLabel = stringResource(R.string.save),
                onClickPositiveButton = { onAction(SnsEditAction.Save) },
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

@Composable
private fun SnsUsernameInput(
    snsType: Sns.SnsType,
    username: String,
    onUsernameChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: SnsError? = null,
) {
    val icon = snsType.icon
    val label = snsType.label
    val centerToTopSize = 24.dp

    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row {
            Icon(
                modifier = Modifier
                    .centerToTop(centerToTopSize)
                    .size(24.dp),
                imageVector = ImageVector.vectorResource(icon),
                tint = Grey30,
                contentDescription = "$label icon",
            )
            FixedWidthText(
                modifier = Modifier
                    .centerToTop(centerToTopSize)
                    .padding(start = 8.dp, end = 12.dp),
                text = label,
                width = 72.dp,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey30,
                shadowColor = MaterialTheme.colorScheme.background,
            )
        }
        BTClearableTextField(
            modifier = Modifier
                .weight(1f)
                .centerToTop(centerToTopSize),
            text = username,
            supportingText = error.message,
            isError = error != null,
            placeholder = stringResource(R.string.sns_username_placeholder),
            onValueChanged = onUsernameChanged,
        )
    }
}

private val SnsError?.message: String?
    @Composable
    get() = when (this) {
        SnsError.ContainsAtSign -> stringResource(R.string.sns_edit_error_contains_at_sign)
        SnsError.ContainsUnsupportedCharacter -> stringResource(R.string.sns_edit_error_contains_unsupported_character)
        null -> null
    }

@Preview
@Composable
private fun SnsUsernameInputPreview() {
    var username by remember { mutableStateOf("") }
    val snsType = Sns.SnsType.INSTAGRAM

    BooltiTheme {
        SnsUsernameInput(
            snsType, username, { username = it },
        )
    }
}

@Preview
@Composable
private fun SnsEditScreenPreview() {
    BooltiTheme {
        SnsEditScreen(
            uiState = SnsEditUiState(),
            onAction = {},
        )
    }
}
