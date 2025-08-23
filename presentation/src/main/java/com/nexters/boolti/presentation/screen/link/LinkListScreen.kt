package com.nexters.boolti.presentation.screen.link

import android.content.ActivityNotFoundException
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.DashedBorderBox
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Orange01
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun LinkListScreen(
    navigateToAddLink: () -> Unit,
    navigateToEditLink: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LinkListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.tryBack()
    }

    LinkListScreen(
        links = uiState.links,
        onClickAdd = { id ->
            if (id != null) {
                navigateToEditLink()
            } else {
                navigateToAddLink()
            }
            viewModel.startAddOrEditLink(id)
        },
        onSave = viewModel::save,
        tryBack = viewModel::tryBack,
        navigateUp = navigateUp,
        event = viewModel.linkListEvent,
        modifier = modifier,
        showActionButton = uiState.isMine,
        actionButtonEnabled = uiState.saveEnabled,
        editing = uiState.editing,
        showExitAlertDialog = uiState.showExitAlertDialog,
        onDismissExitAlertDialog = viewModel::disMissExitAlertDialog,
        setEditMode = viewModel::setEditMode,
        onReorder = viewModel::reorder,
    )
}

@Composable
private fun LinkListScreen(
    links: List<Link>,
    onClickAdd: (id: String?) -> Unit,
    onSave: () -> Unit,
    tryBack: () -> Unit,
    navigateUp: () -> Unit,
    event: Flow<LinkListEvent>,
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

    val linkAddMsg = stringResource(R.string.link_add_msg)
    val linkEditMsg = stringResource(R.string.link_edit_msg)
    val linkRemoveMsg = stringResource(R.string.link_remove_msg)

    ObserveAsEvents(event) {
        when (it) {
            is LinkListEvent.Added -> {
                snackbarHostState.showMessage(linkAddMsg)
            }

            is LinkListEvent.Edited -> {
                snackbarHostState.showMessage(linkEditMsg)
            }

            is LinkListEvent.Removed -> {
                snackbarHostState.showMessage(linkRemoveMsg)
            }

            is LinkListEvent.Finish -> navigateUp()
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
                title = stringResource(R.string.link),
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
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (links.isEmpty()) {
                EmptyLinksContent(
                    onClickAdd = { onClickAdd(null) }
                )
            } else {
                LinksContent(
                    links = links,
                    editing = editing,
                    reorderableState = reorderableState,
                    reorderable = editing,
                    onClickAdd = { id -> onClickAdd(id) },
                    onClickLink = { id ->
                        if (editing) {
                            onClickAdd(id)
                        } else {
                            try {
                                uriHandler.openUri(links.first { it.id == id }.url)
                            } catch (e: ActivityNotFoundException) {
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
private fun EmptyLinksContent(
    onClickAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DashedBorderBox(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 20.dp,
                horizontal = marginHorizontal
            )
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClickAdd),
        cornerRadius = 4.dp,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 34.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_stepper_plus),
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.do_add),
                style = MaterialTheme.typography.bodyLarge,
                color = Grey30,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun LinksContent(
    links: List<Link>,
    editing: Boolean,
    reorderable: Boolean,
    reorderableState: ReorderableLazyListState,
    onClickAdd: (id: String?) -> Unit,
    onClickLink: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp, horizontal = marginHorizontal),
    ) {
        ListToolbar(
            totalCount = links.size,
            onClickAdd = if (editing) {
                { onClickAdd(null) }
            } else {
                null
            },
        )

        LinkItems(
            modifier = Modifier
                .padding(top = 4.dp)
                .weight(1f),
            links = links,
            onClick = onClickLink,
            reorderableState = reorderableState,
            reorderable = reorderable,
        )
    }
}

@Composable
private fun ListToolbar(
    totalCount: Int,
    onClickAdd: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = (8.5).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.list_total_count, totalCount),
            style = MaterialTheme.typography.bodyMedium,
            color = Grey30,
            fontWeight = FontWeight.Normal,
        )

        if (onClickAdd != null) {
            Row(
                modifier = Modifier.clickable(onClick = onClickAdd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_stepper_plus),
                    tint = Orange01,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.do_add),
                    color = Orange01,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun LinkItems(
    links: List<Link>,
    onClick: (id: String) -> Unit,
    reorderable: Boolean,
    reorderableState: ReorderableLazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = reorderableState.listState,
        modifier = modifier
            .reorderable(reorderableState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = links,
            key = { it.id },
        ) { link ->
            ReorderableItem(
                state = reorderableState,
                key = link.id,
            ) {
                LinkItem(
                    modifier = Modifier.clickable(onClick = { onClick(link.id) }),
                    link = link,
                    showHandle = reorderable,
                    reorderableState = reorderableState,
                )
            }
        }
    }
}

@Composable
private fun LinkItem(
    link: Link,
    showHandle: Boolean,
    reorderableState: ReorderableLazyListState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_link),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.weight(1f),
            text = link.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (showHandle) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .detectReorder(state = reorderableState),
                imageVector = ImageVector.vectorResource(R.drawable.ic_reordable_handle),
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun EmptyLinksContentPreview() {
    BooltiTheme {
        EmptyLinksContent({})
    }
}
