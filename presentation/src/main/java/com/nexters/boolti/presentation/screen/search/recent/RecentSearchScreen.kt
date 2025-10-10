package com.nexters.boolti.presentation.screen.search.recent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtSearchBar
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents
import kotlinx.coroutines.launch

@Composable
fun RecentSearchScreen(
    navigateBack: () -> Unit,
    search: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecentSearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val emptyKeywordMessage = stringResource(R.string.search_search_empty)
    val snackbarController = LocalSnackbarController.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.event) {
        when (it) {
            is RecentSearchEvent.EmptyKeyword -> {
                scope.launch {
                    snackbarController.showMessage(emptyKeywordMessage)
                }
            }

            is RecentSearchEvent.Search -> search(it.keyword)
        }
    }

    RecentSearchScreen(
        keyword = uiState.keyword,
        onKeywordChanged = { keyword ->
            viewModel.onIntent(RecentSearchIntent.ChangeKeyword(keyword))
        },
        recentKeywords = uiState.recentSearchKeywords,
        deleteKeyword = { keyword ->
            viewModel.onIntent(RecentSearchIntent.DeleteSearchHistory(keyword))
        },
        onClickClearButton = {
            viewModel.onIntent(RecentSearchIntent.ShowClearHistoriesDialog)
        },
        onClickClear = {
            viewModel.onIntent(RecentSearchIntent.ClearHistories)
        },
        showClearDialog = uiState.showClearDialog,
        showClearButton = uiState.showClearButton,
        dismissClearDialog = {
            viewModel.onIntent(RecentSearchIntent.DismissClearHistoriesDialog)
        },
        navigateBack = navigateBack,
        search = { keyword ->
            viewModel.onIntent(RecentSearchIntent.Search(keyword))
        },
        modifier = modifier,
    )
}

@Composable
private fun RecentSearchScreen(
    keyword: String,
    onKeywordChanged: (String) -> Unit,
    recentKeywords: List<String>,
    deleteKeyword: (keyword: String) -> Unit,
    onClickClearButton: () -> Unit,
    onClickClear: () -> Unit,
    showClearDialog: Boolean,
    showClearButton: Boolean,
    dismissClearDialog: () -> Unit,
    navigateBack: () -> Unit,
    search: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.menu_search),
                onClickBack = navigateBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal),
        ) {
            BtSearchBar(
                keyword = keyword,
                onKeywordChanged = onKeywordChanged,
                hint = stringResource(R.string.search_search_hint),
                search = { search(keyword) },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            )

            if (keyword.isEmpty()) {
                EmptyKeywordContent(
                    recentKeywords = recentKeywords,
                    showClearButton = showClearButton,
                    onClickKeyword = { search(it) },
                    onClickDeleteButton = deleteKeyword,
                    onClickClearButton = onClickClearButton,
                )
            } else {
                SearchingContent(
                    keyword = keyword,
                    onClickKeyword = search,
                )
            }
        }

        if (showClearDialog) {
            ClearDialog(
                onDismiss = dismissClearDialog,
                onClickClear = onClickClear,
            )
        }
    }
}

@Composable
private fun EmptyKeywordContent(
    recentKeywords: List<String>,
    showClearButton: Boolean,
    onClickKeyword: (keyword: String) -> Unit,
    onClickDeleteButton: (keyword: String) -> Unit,
    onClickClearButton: () -> Unit,
) {
    RecentSearchHistoryLabel(
        modifier = Modifier.padding(top = 12.dp),
    )

    Spacer(Modifier.height(4.dp))

    if (recentKeywords.isEmpty()) {
        EmptySearchHistory(modifier = Modifier.padding(top = 12.dp))
    } else {
        RecentSearchHistories(
            histories = recentKeywords,
            onClickKeyword = onClickKeyword,
            onClickDeleteButton = onClickDeleteButton,
            onClickClearButton = onClickClearButton,
            showClearButton = showClearButton,
        )
    }
}

@Composable
private fun RecentSearchHistoryLabel(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.search_recent_keyword),
        style = MaterialTheme.typography.bodySmall,
        color = Grey30,
    )
}

@Composable
private fun EmptySearchHistory(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.search_no_recent_keyword),
        style = MaterialTheme.typography.bodyLarge,
        color = Grey70,
    )
}

@Composable
private fun RecentSearchHistories(
    histories: List<String>,
    showClearButton: Boolean,
    onClickKeyword: (keyword: String) -> Unit,
    onClickDeleteButton: (keyword: String) -> Unit,
    onClickClearButton: () -> Unit,
) {
    histories.forEachIndexed { index, keyword ->
        if (index > 0) {
            HorizontalDivider(
                thickness = 1.dp,
                color = Grey85,
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = { onClickKeyword(keyword) })
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = keyword,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey05,
            )
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(24.dp)
                    .clickable { onClickDeleteButton(keyword) },
                imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                tint = Grey60,
                contentDescription = stringResource(R.string.btn_delete),
            )
        }
    }

    if (showClearButton) {
        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onClickClearButton)
                .padding(vertical = (8.5).dp),
            text = stringResource(R.string.btn_delete_all),
            style = MaterialTheme.typography.bodyMedium,
            color = Grey50,
        )
    }
}

@Composable
private fun ClearDialog(
    onClickClear: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BTDialog(
        modifier = modifier,
        showCloseButton = false,
        enableDismiss = true,
        onDismiss = onDismiss,
        onClickNegativeButton = onDismiss,
        onClickPositiveButton = onClickClear,
        positiveButtonLabel = stringResource(R.string.btn_delete_all),
        negativeButtonLabel = stringResource(R.string.btn_delete),
    ) {
        Text(
            text = stringResource(R.string.search_clear_history_dialog),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SearchingContent(
    keyword: String,
    onClickKeyword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

}

@Preview
@Composable
private fun RecentSearchScreenPreview_Empty() {
    BooltiTheme {
        RecentSearchScreen(
            keyword = "",
            onKeywordChanged = {},
            recentKeywords = listOf("불목", "서강대", "이한세"),
            deleteKeyword = {},
            onClickClearButton = {},
            onClickClear = {},
            showClearDialog = false,
            showClearButton = true,
            dismissClearDialog = {},
            navigateBack = {},
            search = {},
        )
    }
}

@Preview
@Composable
private fun RecentSearchScreenPreview_EmptyHistory() {
    BooltiTheme {
        RecentSearchScreen(
            keyword = "",
            onKeywordChanged = {},
            recentKeywords = emptyList(),
            deleteKeyword = {},
            onClickClearButton = {},
            onClickClear = {},
            showClearDialog = false,
            showClearButton = false,
            dismissClearDialog = {},
            navigateBack = {},
            search = {},
        )
    }
}

@Preview
@Composable
private fun RecentSearchScreenPreview_Searching() {
    BooltiTheme {
        RecentSearchScreen(
            keyword = "검색중",
            onKeywordChanged = {},
            recentKeywords = emptyList(),
            deleteKeyword = {},
            onClickClearButton = {},
            onClickClear = {},
            showClearDialog = false,
            showClearButton = false,
            dismissClearDialog = {},
            navigateBack = {},
            search = {},
        )
    }
}

@Preview
@Composable
private fun RecentSearchScreenPreview_ClearDialog() {
    BooltiTheme {
        RecentSearchScreen(
            keyword = "",
            onKeywordChanged = {},
            recentKeywords = listOf("불목", "서강대", "이한세"),
            deleteKeyword = {},
            onClickClearButton = {},
            onClickClear = {},
            showClearDialog = true,
            showClearButton = true,
            dismissClearDialog = {},
            navigateBack = {},
            search = {},
        )
    }
}
