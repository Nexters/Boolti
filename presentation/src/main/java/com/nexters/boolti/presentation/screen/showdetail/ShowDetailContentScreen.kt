package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.UrlParser

@Composable
fun ShowDetailContentScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = hiltViewModel(),
) {
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notice = uiState.showDetail.notice
    val urlParser = UrlParser(notice)

    Scaffold(
        modifier = modifier,
        topBar = {
            BtBackAppBar(
                title = stringResource(id = R.string.ticketing_all_content_title),
                onClickBack = onBackPressed,
            )
        },
    ) { innerPadding ->
        ClickableText(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal)
                .padding(top = 20.dp),
            text = urlParser.annotatedString,
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        ) { offset ->
            val urlOffset = urlParser.urlOffsets.find { (start, end) -> offset in start..<end }
            if (urlOffset == null) return@ClickableText
            val (start, end) = urlOffset
            val url = notice.substring(start, end)

            uriHandler.openUri(url)
        }
    }
}
