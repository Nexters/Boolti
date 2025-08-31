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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTClearableTextField
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun UserCodeEditScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    viewModel: UserCodeEditViewModel = hiltViewModel(),
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

    UserCodeEditScreen(
        modifier = modifier,
        navigateUp = dismissDialogAndNavigateUp,
        userCode = uiState.userCode,
        showExitAlertDialog = uiState.showExitAlertDialog,
        event = event,
        onDismissExitAlertDialog = viewModel::dismissExitAlertDialog,
        tryBack = {
            val canExit = viewModel.checkCanExit()
            if (canExit) dismissDialogAndNavigateUp()
        },
        onChangeUserCode = viewModel::changeUserCode,
        nicknameError = uiState.userCodeError,
        saveEnabled = uiState.saveEnabled,
        onSave = viewModel::saveUserCode,
    )
}

@Composable
private fun UserCodeEditScreen(
    userCode: UserCode,
    onChangeUserCode: (String) -> Unit,
    nicknameError: UserCodeError?,
    saveEnabled: Boolean,
    showExitAlertDialog: Boolean,
    event: Flow<UserCodeEditEvent>,
    tryBack: () -> Unit, // 이탈 가능 상태 확인 후 이탈
    navigateUp: () -> Unit, // 진짜로 화면 이탈
    onSave: () -> Unit,
    onDismissExitAlertDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ObserveAsEvents(event) {
        when (it) {
            UserCodeEditEvent.Saved -> navigateUp()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = tryBack,
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                title = stringResource(R.string.label_id),
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.save_short),
                        onClick = onSave,
                        enabled = saveEnabled,
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
                text = userCode,
                onValueChanged = onChangeUserCode,
                isError = nicknameError != null,
                supportingText = nicknameError?.let {
                    when (it) {
                        UserCodeError.MinLength -> stringResource(
                            R.string.validate_min_length,
                            4,
                        )

                        UserCodeError.ContainsWhitespace -> stringResource(R.string.validate_whitespace)

                        UserCodeError.Invalid -> stringResource(R.string.validate_edit_usercode)
                        UserCodeError.Duplicated -> stringResource(R.string.validate_duplicated_usercode)
                    }
                },
                placeholder = stringResource(R.string.nickname_edit_placeholder),
                singleLine = true,
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

@Preview
@Composable
private fun UserCodeEditScreenPreview() {
    var userCode by remember { mutableStateOf("A1234") }
    BooltiTheme {
        UserCodeEditScreen(
            userCode = userCode,
            onChangeUserCode = { userCode = it },
            nicknameError = null,
            saveEnabled = false,
            showExitAlertDialog = false,
            event = emptyFlow(),
            onDismissExitAlertDialog = {},
            tryBack = {},
            navigateUp = {},
            onSave = {},
        )
    }
}
