package com.nexters.boolti.presentation.screen.profileedit.introduce

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.extension.takeForUnicode
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun IntroduceEditScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    viewModel: IntroduceEditViewModel = hiltViewModel(),
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

    IntroduceEditScreen(
        modifier = modifier,
        navigateUp = dismissDialogAndNavigateUp,
        introduce = uiState.introduce,
        maxLength = viewModel.maxLength,
        showExitAlertDialog = uiState.showExitAlertDialog,
        event = event,
        onDismissExitAlertDialog = viewModel::dismissExitAlertDialog,
        tryBack = {
            val canExit = viewModel.checkCanExit()
            if (canExit) dismissDialogAndNavigateUp()
        },
        onChangeIntroduce = viewModel::changeIntroduction,
        saveEnabled = uiState.saveEnabled,
        onSave = viewModel::saveIntroduction,
    )
}

@Composable
private fun IntroduceEditScreen(
    introduce: String,
    onChangeIntroduce: (String) -> Unit,
    maxLength: Int,
    saveEnabled: Boolean,
    showExitAlertDialog: Boolean,
    event: Flow<IntroduceEditEvent>,
    tryBack: () -> Unit, // 이탈 가능 상태 확인 후 이탈
    navigateUp: () -> Unit, // 진짜로 화면 이탈
    onSave: () -> Unit,
    onDismissExitAlertDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ObserveAsEvents(event) {
        when (it) {
            IntroduceEditEvent.Saved -> navigateUp()
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
                title = stringResource(R.string.label_introduction),
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
            BTTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .padding(horizontal = marginHorizontal),
                text = introduce.takeForUnicode(maxLength),
                placeholder = stringResource(R.string.introduce_edit_placeholder),
                minHeight = 160.dp,
                bottomEndText = "${introduce.length}/${maxLength}자",
                onValueChanged = {
                    onChangeIntroduce(it.takeForUnicode(maxLength))
                },
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
private fun NicknameEditScreenPreview() {
    var introduce by remember { mutableStateOf("mangbaam") }
    BooltiTheme {
        IntroduceEditScreen(
            introduce = introduce,
            maxLength = 60,
            onChangeIntroduce = { introduce = it },
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
