package com.nexters.boolti.presentation.screen.video

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BTTextFieldDefaults
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents

@Composable
fun VideoEditScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = viewModel.videoEditEvent

    ObserveAsEvents(event) {
        when (it) {
            VideoEditEvent.Finish -> navigateUp()
        }
    }

    VideoEditScreen(
        isEditMode = uiState.editingVideo?.id?.isNotEmpty() == true,
        videoUrl = uiState.editingVideo?.url.orEmpty(),
        onClickBack = navigateUp,
        onClickComplete = viewModel::completeAddOrEditVideo,
        onChangeVideoUrl = viewModel::onVideoUrlChanged,
        requireRemove = viewModel::removeVideo,
        modifier = modifier,
    )
}

@Composable
fun VideoEditScreen(
    isEditMode: Boolean,
    videoUrl: String,
    onClickBack: () -> Unit,
    onClickComplete: () -> Unit,
    onChangeVideoUrl: (String) -> Unit,
    requireRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showVideoRemoveDialog by remember { mutableStateOf(false) }
    val videoUrlInteractionSource = remember { MutableInteractionSource() }
    val videoUrlFocused by videoUrlInteractionSource.collectIsFocusedAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = onClickBack,
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.complete),
                        onClick = onClickComplete,
                        enabled = videoUrl.isNotBlank(),
                    )
                },
                title = stringResource(if (isEditMode) R.string.video_edit else R.string.video_add),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Row(
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.defaultMinSize(minWidth = 64.dp),
                        text = "URL",
                        color = Grey30,
                    )
                    BTTextField(
                        modifier = Modifier.padding(start = 12.dp),
                        text = videoUrl,
                        placeholder = "URL을 첨부해 주세요",
                        singleLine = true,
                        onValueChanged = onChangeVideoUrl,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Default,
                        ),
                        trailingIcon = if (videoUrlFocused && videoUrl.isNotEmpty()) {
                            { BTTextFieldDefaults.ClearButton(onClick = { onChangeVideoUrl("") }) }
                        } else {
                            null
                        },
                        interactionSource = videoUrlInteractionSource,
                    )
                }
            }
            if (isEditMode) {
                MainButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = marginHorizontal)
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    label = stringResource(R.string.video_remove),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentColor = Grey90,
                    ),
                    onClick = { showVideoRemoveDialog = true },
                )
            }
        }

        if (showVideoRemoveDialog) {
            BTDialog(
                positiveButtonLabel = stringResource(R.string.btn_delete),
                negativeButtonLabel = stringResource(R.string.cancel),
                onClickPositiveButton = {
                    requireRemove()
                    showVideoRemoveDialog = false
                },
                onClickNegativeButton = { showVideoRemoveDialog = false },
                onDismiss = { showVideoRemoveDialog = false },
            ) {
                Text(stringResource(R.string.remove_video_dialog_message))
            }
        }
    }
}

@Preview
@Composable
private fun VideoEditScreenPreview() {
    BooltiTheme {
        VideoEditScreen(
            isEditMode = true,
            videoUrl = "https://www.youtube.com/watch?v=example",
            onClickBack = {},
            onClickComplete = {},
            onChangeVideoUrl = {},
            requireRemove = {},
        )
    }
}