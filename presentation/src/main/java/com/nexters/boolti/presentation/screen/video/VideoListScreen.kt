package com.nexters.boolti.presentation.screen.video

import android.content.ActivityNotFoundException
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.EmptyListAddButton
import com.nexters.boolti.presentation.component.ListToolbar
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey90
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
    viewModel: VideoListViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.onAction(VideoListAction.Back)
    }

    VideoListEventEffect(viewModel.event)

    VideoListScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

/** 목록·편집 화면이 ViewModel을 함께 쓰므로 Event 처리도 같은 함수로 한다 */
@Composable
internal fun VideoListEventEffect(event: Flow<VideoListEvent>) {
    val navController = LocalNavController.current
    val snackbarController = LocalSnackbarController.current
    val videoAddMsg = stringResource(R.string.video_add_msg)
    val videoEditMsg = stringResource(R.string.video_edit_msg)
    val videoDeleteMsg = stringResource(R.string.video_delete_msg)

    ObserveAsEvents(event) {
        when (it) {
            VideoListEvent.Added -> snackbarController.showMessage(videoAddMsg)
            VideoListEvent.Edited -> snackbarController.showMessage(videoEditMsg)
            VideoListEvent.Removed -> snackbarController.showMessage(videoDeleteMsg)
            is VideoListEvent.NavigateToEdit -> navController.navigate(VideoListRoute.VideoEdit(it.isEditMode))
            VideoListEvent.CloseEdit -> navController.popBackStack<VideoListRoute.VideoEdit>(inclusive = true)
            VideoListEvent.Finish -> navController.popBackStack<VideoListRoute.VideoListRoot>(inclusive = true)
        }
    }
}

@Composable
private fun VideoListScreen(
    uiState: VideoListUiState,
    onAction: (VideoListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val videos = uiState.videos
    val editing = uiState.editing
    val reorderableState = rememberReorderableLazyListState(
        onMove = { from, to ->
            onAction(VideoListAction.Reorder(from.index, to.index))
        },
    )

    val snackbarHostState = LocalSnackbarController.current

    val uriHandler = LocalUriHandler.current
    val invalidUrlMsg = stringResource(R.string.invalid_link)

    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = { onAction(VideoListAction.Back) },
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                title = stringResource(R.string.video),
                actionButtons = {
                    when {
                        !uiState.isMine -> Unit
                        editing -> BtAppBarDefaults.AppBarTextButton(
                            label = stringResource(R.string.save_short),
                            enabled = uiState.saveEnabled,
                            onClick = { onAction(VideoListAction.Save) },
                        )

                        else -> BtAppBarDefaults.AppBarIconButton(
                            iconRes = R.drawable.ic_edit_pen,
                            onClick = { onAction(VideoListAction.StartEditing) },
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
            if (uiState.loading) {
                BtCircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (videos.isEmpty()) {
                EmptyListAddButton(
                    onClickAdd = { onAction(VideoListAction.ClickAddVideo) }
                )
            } else {
                VideosContent(
                    videos = videos,
                    editing = editing,
                    reorderableState = reorderableState,
                    reorderable = editing,
                    onClickAdd = { onAction(VideoListAction.ClickAddVideo) },
                    onClickVideo = { localId ->
                        if (editing) {
                            onAction(VideoListAction.ClickVideo(localId))
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

        if (uiState.showExitAlertDialog) {
            BTDialog(
                enableDismiss = true,
                showCloseButton = true,
                onDismiss = { onAction(VideoListAction.DismissExitAlertDialog) },
                negativeButtonLabel = stringResource(R.string.btn_exit),
                onClickNegativeButton = { onAction(VideoListAction.Exit) },
                positiveButtonLabel = stringResource(R.string.save),
                onClickPositiveButton = { onAction(VideoListAction.Save) },
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
    onClickAdd: () -> Unit,
    onClickVideo: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp, horizontal = marginHorizontal),
    ) {
        ListToolbar(
            totalCount = videos.size,
            onClickAdd = if (editing) {
                onClickAdd
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
                    onClick = { onClick(video.localId) },
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    reorderableState: ReorderableLazyListState? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
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
                text = video.title.ifEmpty { stringResource(R.string.unknown_video) },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp,
                maxLines = 2,
            )
            Text(
                text = video.duration.ifEmpty { "-" },
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
        }

        val reorderableModifier = if (reorderableState != null) {
            Modifier.detectReorder(state = reorderableState)
        } else {
            Modifier
        }

        if (showHandle) {
            Icon(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .then(reorderableModifier),
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
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = thumbnailUrl.takeIf { it.isNotEmpty() },
            contentDescription = description?.ifEmpty { stringResource(R.string.unknown_video) },
            error = {
                // fallback UI 표시
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Grey80),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier.size(width = 77.dp, height = 29.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_logo_boolti),
                        tint = Grey90,
                        contentDescription = stringResource(R.string.description_app_logo),
                    )
                }
            }
        )
    }
}
