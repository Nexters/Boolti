package com.nexters.boolti.presentation.screen.link

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.Flow

@Composable
fun LinkListScreen2(
    navigateToAddLink: () -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LinkListViewModel2 = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LinkListScreen2(
        links = uiState.links,
        onClickAdd = navigateToAddLink,
        onSave = viewModel::save,
        tryBack = viewModel::tryBack,
        navigateUp = navigateUp,
        event = viewModel.event,
        modifier = modifier,
    )
}

@Composable
private fun LinkListScreen2(
    links: List<Link>,
    onClickAdd: () -> Unit,
    onSave: () -> Unit,
    tryBack: () -> Unit,
    navigateUp: () -> Unit,
    event: Flow<LinkListEvent2>,
    modifier: Modifier = Modifier,
    showExitAlertDialog: Boolean = false,
    onDismissExitAlertDialog: () -> Unit = {},
) {
    ObserveAsEvents(event) {
        when (it) {
            LinkListEvent2.Added -> {}
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
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.save_short),
                        onClick = onSave,
                    )
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
                    onClickAdd = onClickAdd
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

@Preview
@Composable
private fun EmptyLinksContentPreview() {
    BooltiTheme {
        EmptyLinksContent({})
    }
}
