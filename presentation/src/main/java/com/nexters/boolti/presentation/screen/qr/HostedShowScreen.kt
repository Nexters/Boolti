package com.nexters.boolti.presentation.screen.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.point1
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun HostedShowScreen(
    onClickBack: () -> Unit,
    onClickShow: (showId: String, showName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HostedShowViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.hostedShowsTitle),
                onClickBack = onClickBack,
            )
        }
    ) { innerPadding ->
        if (uiState.shows.isEmpty()) {
            EmptyHostedShow(modifier = modifier.padding(innerPadding))
        } else {
            HostedShows(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                shows = uiState.shows,
                onClick = onClickShow,
            )
        }
    }
}

@Composable
fun HostedShows(
    modifier: Modifier = Modifier,
    shows: List<Show>,
    onClick: (showId: String, showName: String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(shows, key = { show -> show.id }) { show ->
            HostedShowItem(show, onClick = onClick)
        }
    }
}

@Composable
private fun HostedShowItem(
    show: Show,
    onClick: (showId: String, showName: String) -> Unit,
) {
    val enable = LocalDate.now().toEpochDay() <= show.date.toLocalDate().toEpochDay()
    val tint = if (enable) White else Grey50

    Row(
        modifier = Modifier
            .clickable(enable) { onClick(show.id, show.name) }
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = show.name,
            style = point1,
            color = tint,
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_scan),
            tint = tint,
            contentDescription = stringResource(R.string.description_qr_icon),
        )
    }
}

@Composable
fun EmptyHostedShow(
    modifier: Modifier,
) {
    ConstraintLayout(
        modifier = modifier,
    ) {
        val columnRef = createRef()
        Column(
            modifier = Modifier.constrainAs(columnRef) {
                centerHorizontallyTo(parent)
                centerVerticallyTo(parent, 0.4f)
            },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.hosted_shows_empty_label),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = stringResource(R.string.hosted_shows_empty_desc),
                style = MaterialTheme.typography.bodyLarge,
                color = Grey30,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.size(28.dp))
        }
    }
}

@Preview
@Composable
fun HostedShowItemPreview() {
    BooltiTheme {
        Surface {
            HostedShowItem(
                Show("", "hello world", LocalDateTime.now(), LocalDate.now(), LocalDate.now(), "")
            ) { _, _ -> }
        }
    }
}

@Preview
@Composable
fun OutDatedHostedShowItemPreview() {
    BooltiTheme {
        Surface {
            HostedShowItem(
                Show(
                    "",
                    "hello world",
                    LocalDateTime.now().minusDays(1),
                    LocalDate.now(),
                    LocalDate.now(),
                    ""
                )
            ) { _, _ -> }
        }
    }
}