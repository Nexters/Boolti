package com.nexters.boolti.presentation.screen.link

import android.content.ActivityNotFoundException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.extension.toValidUrlString
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.profile.LinkItem
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun LinkListScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    viewModel: LinkListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LinkListScreen(
        modifier = modifier,
        links = uiState.links,
        loading = uiState.loading,
        event = viewModel.event,
        onClickBack = onClickBack,
    )
}

@Composable
private fun LinkListScreen(
    links: List<Link>,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    event: Flow<LinkListEvent> = emptyFlow(),
    onClickBack: () -> Unit = {},
) {
    val snackbarController = LocalSnackbarController.current
    val uriHandler = LocalUriHandler.current
    val unknownErrorMsg = stringResource(R.string.message_unknown_error)
    val invalidUrlMsg = stringResource(R.string.invalid_link)

    ObserveAsEvents(event) {
        when (it) {
            LinkListEvent.LoadFailed -> snackbarController.showMessage(unknownErrorMsg) // TODO 추후 에러 공통화 필요
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.profile_links_title),
                onClickBack = onClickBack
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = marginHorizontal)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 20.dp),
            ) {
                items(links) { link ->
                    LinkItem(link = link) {
                        try {
                            uriHandler.openUri(link.url.toValidUrlString())
                        } catch (e: ActivityNotFoundException) {
                            snackbarController.showMessage(invalidUrlMsg)
                        }
                    }
                }
            }
            if (loading) {
                BtCircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                )
            }
        }
    }
}

@Preview
@Composable
private fun LinkListPreview() {
    val links = listOf(
        Link("1", "네이버1", ""),
        Link("2", "네이버2", ""),
        Link("3", "네이버3", ""),
        Link("4", "네이버4", ""),
        Link("5", "네이버5", ""),
        Link("6", "네이버6", ""),
        Link("7", "네이버7", ""),
    )

    BooltiTheme {
        LinkListScreen(links = links, loading = true)
    }
}
