package com.nexters.boolti.presentation.screen.perforemdshows

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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.ShowItem
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun PerformedShowsScreen(
    modifier: Modifier = Modifier,
    onClickShow: (show: Show) -> Unit,
    viewModel: PerformedShowsViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PerformedShowsScreen(
        modifier = modifier,
        loading = uiState.loading,
        shows = uiState.shows,
        event = viewModel.event,
        onClickShow = onClickShow,
        onClickBack = onClickBack,
    )
}

@Composable
private fun PerformedShowsScreen(
    shows: List<Show>,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    event: Flow<PerformedShowsEvent> = emptyFlow(),
    onClickShow: (show: Show) -> Unit = {},
    onClickBack: () -> Unit,
) {
    val snackbarController = LocalSnackbarController.current
    val unknownErrorMsg = stringResource(R.string.message_unknown_error)

    ObserveAsEvents(event) {
        when (it) {
            PerformedShowsEvent.FetchFailed -> snackbarController.showMessage(unknownErrorMsg) // TODO 추후 에러 공통화 필요
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.performed_shows),
                onClickBack = onClickBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(shows) { show ->
                    ShowItem(
                        show = show,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        onClick = { onClickShow(show) },
                    )
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
