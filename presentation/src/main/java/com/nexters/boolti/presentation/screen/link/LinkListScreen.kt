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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.EmptyListAddButton
import com.nexters.boolti.presentation.component.ListToolbar
import com.nexters.boolti.presentation.extension.toValidUrlString
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import com.nexters.boolti.presentation.theme.BooltiTheme
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
    viewModel: LinkListViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.onAction(LinkListAction.Back)
    }

    LinkListEventEffect(viewModel.event)

    LinkListScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

/** 목록·편집 화면이 ViewModel을 함께 쓰므로 Event 처리도 같은 함수로 한다 */
@Composable
internal fun LinkListEventEffect(event: Flow<LinkListEvent>) {
    val navController = LocalNavController.current
    val snackbarController = LocalSnackbarController.current
    val linkAddMsg = stringResource(R.string.link_add_msg)
    val linkEditMsg = stringResource(R.string.link_edit_msg)
    val linkRemoveMsg = stringResource(R.string.link_remove_msg)

    ObserveAsEvents(event) {
        when (it) {
            LinkListEvent.Added -> snackbarController.showMessage(linkAddMsg)
            LinkListEvent.Edited -> snackbarController.showMessage(linkEditMsg)
            LinkListEvent.Removed -> snackbarController.showMessage(linkRemoveMsg)
            LinkListEvent.NavigateToEdit -> navController.navigate(LinkListRoute.LinkEdit)
            LinkListEvent.CloseEdit -> navController.popBackStack<LinkListRoute.LinkEdit>(inclusive = true)
            LinkListEvent.Finish -> navController.popBackStack<LinkListRoute.LinkListRoot>(inclusive = true)
        }
    }
}

@Composable
private fun LinkListScreen(
    uiState: LinkListUiState,
    onAction: (LinkListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val links = uiState.links
    val editing = uiState.editing
    val reorderableState = rememberReorderableLazyListState(
        onMove = { from, to ->
            onAction(LinkListAction.Reorder(from.index, to.index))
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
                        onClick = { onAction(LinkListAction.Back) },
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                title = stringResource(R.string.link),
                actionButtons = {
                    when {
                        !uiState.isMine -> Unit
                        editing -> BtAppBarDefaults.AppBarTextButton(
                            label = stringResource(R.string.save_short),
                            enabled = uiState.saveEnabled,
                            onClick = { onAction(LinkListAction.Save) },
                        )

                        else -> BtAppBarDefaults.AppBarIconButton(
                            iconRes = R.drawable.ic_edit_pen,
                            onClick = { onAction(LinkListAction.StartEditing) },
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
                    onClickAdd = { onAction(LinkListAction.ClickAddLink) }
                )
            } else {
                LinksContent(
                    links = links,
                    editing = editing,
                    reorderableState = reorderableState,
                    reorderable = editing,
                    onClickAdd = { onAction(LinkListAction.ClickAddLink) },
                    onClickLink = { id ->
                        if (editing) {
                            onAction(LinkListAction.ClickLink(id))
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

        if (uiState.showExitAlertDialog) {
            BTDialog(
                enableDismiss = true,
                showCloseButton = true,
                onDismiss = { onAction(LinkListAction.DismissExitAlertDialog) },
                negativeButtonLabel = stringResource(R.string.btn_exit),
                onClickNegativeButton = { onAction(LinkListAction.Exit) },
                positiveButtonLabel = stringResource(R.string.save),
                onClickPositiveButton = { onAction(LinkListAction.Save) },
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
    onClickAdd: () -> Unit,
    onClickLink: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp, horizontal = marginHorizontal),
    ) {
        ListToolbar(
            totalCount = links.size,
            onClickAdd = if (editing) {
                onClickAdd
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
