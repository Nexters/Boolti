package com.nexters.boolti.presentation.screen.video

import android.content.ActivityNotFoundException
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.EmptyListAddButton
import com.nexters.boolti.presentation.component.ListToolbar
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun VideoListScreen(
    navigateToAddVideo: () -> Unit,
    navigateToEditVideo: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideoListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.tryBack()
    }

    VideoListScreen(
        videos = uiState.videos,
        onClickAdd = { id ->
            if (id != null) {
                navigateToEditVideo()
            } else {
                navigateToAddVideo()
            }
            viewModel.startAddOrEditVideo(id)
        },
        onSave = viewModel::save,
        tryBack = viewModel::tryBack,
        navigateUp = navigateUp,
        event = viewModel.videoListEvent,
        modifier = modifier,
        showActionButton = uiState.isMine,
        actionButtonEnabled = uiState.saveEnabled,
        editing = uiState.editing,
        showExitAlertDialog = uiState.showExitAlertDialog,
        onDismissExitAlertDialog = viewModel::dismissExitAlertDialog,
        setEditMode = viewModel::setEditMode,
        onReorder = viewModel::reorder,
    )
}

@Composable
private fun VideoListScreen(
    videos: List<YouTubeVideo>,
    onClickAdd: (id: String?) -> Unit,
    onSave: () -> Unit,
    tryBack: () -> Unit,
    navigateUp: () -> Unit,
    event: Flow<VideoListEvent>,
    modifier: Modifier = Modifier,
    showActionButton: Boolean = false,
    actionButtonEnabled: Boolean = false,
    editing: Boolean = false,
    showExitAlertDialog: Boolean = false,
    onDismissExitAlertDialog: () -> Unit = {},
    setEditMode: () -> Unit = {},
    onReorder: (from: Int, to: Int) -> Unit = { _, _ -> },
) {
    val reorderableState = rememberReorderableLazyListState(
        onMove = { from, to ->
            onReorder(from.index, to.index)
        },
    )

    val snackbarHostState = LocalSnackbarController.current

    val uriHandler = LocalUriHandler.current
    val unknownErrorMsg = stringResource(R.string.message_unknown_error)
    val invalidUrlMsg = stringResource(R.string.invalid_link)

    val videoAddMsg = stringResource(R.string.video_add_msg)
    val videoEditMsg = stringResource(R.string.video_edit_msg)
    val videoDeleteMsg = stringResource(R.string.video_delete_msg)

    ObserveAsEvents(event) {
        when (it) {
            is VideoListEvent.Added -> {
                snackbarHostState.showMessage(videoAddMsg)
            }

            is VideoListEvent.Edited -> {
                snackbarHostState.showMessage(videoEditMsg)
            }

            is VideoListEvent.Removed -> {
                snackbarHostState.showMessage(videoDeleteMsg)
            }

            is VideoListEvent.Finish -> navigateUp()
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
                title = stringResource(R.string.video),
                actionButtons = {
                    when {
                        !showActionButton -> Unit
                        editing -> BtAppBarDefaults.AppBarTextButton(
                            label = stringResource(R.string.save_short),
                            enabled = actionButtonEnabled,
                            onClick = onSave,
                        )

                        else -> BtAppBarDefaults.AppBarIconButton(
                            iconRes = R.drawable.ic_edit_pen,
                            onClick = setEditMode,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (videos.isEmpty()) {
                EmptyListAddButton(
                    onClickAdd = { onClickAdd(null) }
                )
            } else {
                VideosContent(
                    videos = videos,
                    editing = editing,
                    reorderableState = reorderableState,
                    reorderable = editing,
                    onClickAdd = { id -> onClickAdd(id) },
                    onClickVideo = { localId ->
                        if (editing) {
                            onClickAdd(localId.ifEmpty { null })
                        } else {
                            try {
                                uriHandler.openUri(videos.first { it.localId == localId }.url)
                            } catch (e: ActivityNotFoundException) {
                                e.printStackTrace()
                                snackbarHostState.showMessage(invalidUrlMsg)
                            } catch (e: IllegalArgumentException) {
                                e.printStackTrace()
                                snackbarHostState.showMessage(invalidUrlMsg)
                            }
                        }
                    }
                )
            }
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
private fun VideosContent(
    videos: List<YouTubeVideo>,
    editing: Boolean,
    reorderable: Boolean,
    reorderableState: ReorderableLazyListState,
    onClickAdd: (id: String?) -> Unit,
    onClickVideo: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp, horizontal = marginHorizontal),
    ) {
        ListToolbar(
            totalCount = videos.size,
            onClickAdd = if (editing) {
                { onClickAdd(null) }
            } else {
                null
            },
        )

        VideoItems(
            modifier = Modifier
                .padding(top = 4.dp)
                .weight(1f),
            videos = videos,
            onClick = onClickVideo,
            reorderableState = reorderableState,
            reorderable = reorderable,
        )
    }
}

@Composable
private fun VideoItems(
    videos: List<YouTubeVideo>,
    onClick: (id: String) -> Unit,
    reorderable: Boolean,
    reorderableState: ReorderableLazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = reorderableState.listState,
        modifier = modifier
            .reorderable(reorderableState),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(
            items = videos,
            key = { it.localId },
        ) { video ->
            ReorderableItem(
                state = reorderableState,
                key = video.localId,
            ) {
                VideoItem(
                    modifier = Modifier
                        .clickable(onClick = { onClick(video.localId) }),
                    video = video,
                    showHandle = reorderable,
                    reorderableState = reorderableState,
                )
            }
        }
    }
}

@Composable
fun VideoItem(
    video: YouTubeVideo,
    showHandle: Boolean,
    reorderableState: ReorderableLazyListState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .height(90.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VideoThumbnail(
            thumbnailUrl = video.thumbnailUrl,
            description = video.title,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                text = video.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
            Text(
                text = video.duration.ifEmpty { "-" },
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
        }

        if (showHandle) {
            Icon(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .detectReorder(state = reorderableState),
                imageVector = ImageVector.vectorResource(R.drawable.ic_reordable_handle),
                tint = Grey70,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun VideoThumbnail(
    thumbnailUrl: String,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = modifier
            .aspectRatio(160 / 90f)
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        // TODO default thumbnail
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = thumbnailUrl,
            contentDescription = description,
        )
    }
}
