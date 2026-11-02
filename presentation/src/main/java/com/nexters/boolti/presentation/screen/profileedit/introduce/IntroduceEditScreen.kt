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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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

@Composable
fun IntroduceEditScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IntroduceEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            IntroduceEditEvent.NavigateUp -> navigateUp()
        }
    }

    BackHandler { viewModel.onAction(IntroduceEditAction.ClickBack) }

    IntroduceEditScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun IntroduceEditScreen(
    uiState: IntroduceEditUiState,
    onAction: (IntroduceEditAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = { onAction(IntroduceEditAction.ClickBack) },
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                title = stringResource(R.string.label_introduction),
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.save_short),
                        onClick = { onAction(IntroduceEditAction.Save) },
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
            BTTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .padding(horizontal = marginHorizontal),
                text = uiState.introduce.takeForUnicode(IntroduceEditUiState.MAX_LENGTH),
                placeholder = stringResource(R.string.introduce_edit_placeholder),
                singleLine = false,
                bottomEndText = "${uiState.introduce.length}/${IntroduceEditUiState.MAX_LENGTH}자",
                onValueChanged = {
                    onAction(IntroduceEditAction.ChangeIntroduce(it.takeForUnicode(IntroduceEditUiState.MAX_LENGTH)))
                },
            )
        }

        if (uiState.showExitAlertDialog) {
            BTDialog(
                enableDismiss = true,
                showCloseButton = true,
                onDismiss = { onAction(IntroduceEditAction.DismissExitAlertDialog) },
                negativeButtonLabel = stringResource(R.string.btn_exit),
                onClickNegativeButton = { onAction(IntroduceEditAction.ConfirmExit) },
                positiveButtonLabel = stringResource(R.string.save),
                onClickPositiveButton = { onAction(IntroduceEditAction.Save) },
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
private fun IntroduceEditScreenPreview() {
    BooltiTheme {
        IntroduceEditScreen(
            uiState = IntroduceEditUiState(introduce = "mangbaam"),
            onAction = {},
        )
    }
}
