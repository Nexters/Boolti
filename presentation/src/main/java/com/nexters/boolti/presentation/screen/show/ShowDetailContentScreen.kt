package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.screen.sharedViewModel
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal

fun NavGraphBuilder.ShowDetailContentScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "content",
    ) { entry ->
        val showViewModel: ShowDetailViewModel =
            entry.sharedViewModel(navController = navController)

        ShowDetailContentScreen(
            modifier = modifier,
            viewModel = showViewModel,
            onBackPressed = { navController.popBackStack() }
        )
    }
}

@Composable
private fun ShowDetailContentScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        topBar = { ShowDetailContentAppBar(onBackPressed = onBackPressed) }
    ) { innerPadding ->
        Text(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal)
                .padding(top = 20.dp),
            text = uiState.showDetail.notice,
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        )
    }
}

@Composable
private fun ShowDetailContentAppBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(color = MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.size(width = 48.dp, height = 44.dp), onClick = onBackPressed
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(id = R.string.description_navigate_back),
                modifier
                    .padding(start = marginHorizontal)
                    .size(width = 24.dp, height = 24.dp)
            )
        }
        Text(
            text = stringResource(id = R.string.ticketing_all_content_title),
            style = MaterialTheme.typography.titleMedium.copy(color = Grey10),
        )
    }
}
