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
import androidx.hilt.navigation.compose.hiltViewModel
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun SnsEditScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    viewModel: SnsEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = viewModel.event
    val dismissDialogAndNavigateUp = {
        viewModel.dismissExitAlertDialog()
        navigateUp()
    }

    BackHandler {
        val canExit = viewModel.checkCanExit()
        if (canExit) dismissDialogAndNavigateUp()
    }

    SnsEditScreen(
        modifier = modifier,
        instagramUsername = uiState.instagramUsername,
        youtubeUsername = uiState.youtubeUsername,
        onChangeInstagramUsername = viewModel::changeInstagramUsername,
        onChangeYoutubeUsername = viewModel::changeYoutubeUsername,
        tryBack = {
            val canExit = viewModel.checkCanExit()
            if (canExit) dismissDialogAndNavigateUp()
        },
        event = event,
        instagramUsernameError = uiState.instagramUsernameError,
        youtubeUsernameError = uiState.youtubeUsernameError,
        showExitAlertDialog = uiState.showExitAlertDialog,
        onDismissExitAlertDialog = dismissDialogAndNavigateUp,
        navigateUp = dismissDialogAndNavigateUp,
        saveEnabled = uiState.saveEnabled,
        onSave = viewModel::saveSns,
    )
}

@Composable
private fun SnsEditScreen(
    modifier: Modifier = Modifier,
    instagramUsername: String = "",
    youtubeUsername: String = "",
    showExitAlertDialog: Boolean = false,
    event: Flow<SnsEditEvent> = emptyFlow(),
    saveEnabled: Boolean = true,
    instagramUsernameError: SnsError? = null,
    youtubeUsernameError: SnsError? = null,
    onChangeInstagramUsername: (String) -> Unit = {},
    onChangeYoutubeUsername: (String) -> Unit = {},
    tryBack: () -> Unit = {}, // 이탈 가능 상태 확인 후 이탈
    navigateUp: () -> Unit = {}, // 진짜로 화면 이탈
    onSave: () -> Unit = {},
    onDismissExitAlertDialog: () -> Unit = {},
) {
    ObserveAsEvents(event) {
        when (it) {
            SnsEditEvent.Saved -> navigateUp()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                title = stringResource(R.string.sns),
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = tryBack,
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.save_short),
                        onClick = onSave,
                        enabled = saveEnabled,
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
                username = instagramUsername,
                onUsernameChanged = onChangeInstagramUsername,
                error = instagramUsernameError,
            )
            SnsUsernameInput(
                snsType = Sns.SnsType.YOUTUBE,
                username = youtubeUsername,
                onUsernameChanged = onChangeYoutubeUsername,
                error = youtubeUsernameError,
            )
        }

        if (showExitAlertDialog) {
            BTDialog(
                enableDismiss = true,
                showCloseButton = true,
                onDismiss = onDismissExitAlertDialog,
                negativeButtonLabel = stringResource(R.string.btn_exit),
                onClickNegativeButton = navigateUp,
                positiveButtonLabel = stringResource(R.string.save),
                onClickPositiveButton = onSave,
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
            singleLine = true,
        )
    }
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
private fun SnsEditPreview() {
    BooltiTheme {
        SnsEditScreen()
    }
}
