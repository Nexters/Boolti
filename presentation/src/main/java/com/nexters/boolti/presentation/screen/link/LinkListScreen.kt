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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.nexters.boolti.presentation.component.EmptyListAddButton
import com.nexters.boolti.presentation.component.ListToolbar
import com.nexters.boolti.presentation.extension.toValidUrlString
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.marginHorizontal
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
        navigateToEditLink = navigateToEditLink,
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
    navigateToEditLink: () -> Unit,
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

    LaunchedEffect(Unit) {
        event.collect {
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

                is LinkListEvent.NavigateToEdit -> navigateToEditLink()
            }
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
                EmptyListAddButton(
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
                                uriHandler.openUri(links.first { it.id == id }.url.toValidUrlString())
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
        EmptyListAddButton({})
    }
}
