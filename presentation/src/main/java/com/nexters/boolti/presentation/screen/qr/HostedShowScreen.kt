package com.nexters.boolti.presentation.screen.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.aggroFamily

@Composable
fun HostedShowScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickShow: (showId: String) -> Unit,
) {
    Scaffold(
        topBar = { HostedShowToolbar(onClickBack) }
    ) { innerPadding ->
        HostedShows(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            onClick = {
                onClickShow("3") // TODO ViewModel 생성 후 showId 는 ViewModel 에서 관리
            }
        )
//        EmptyHostedShow(modifier = modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HostedShowToolbar(
    onClickBack: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.hostedShowsTitle))
        }, navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.description_navigate_back),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
        )
    )
}

@Composable
fun HostedShows(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(3) {
            HostedShowItem(onClick = onClick)
        }
    }
}

@Composable
private fun HostedShowItem(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "일이삼사오육칠팔구십일이삼사오육칠팔구십일이삼사오육칠팔구십일이삼사오육칠팔구십",
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = aggroFamily,
        )
        IconButton(onClick = onClick) {
            Icon(painter = painterResource(id = R.drawable.ic_scan), contentDescription = "QR 스캔 아이콘")
        }
    }
}

@Preview
@Composable
fun HostedShowItemPreview() {
    BooltiTheme {
        Surface {
            HostedShowItem {}
        }
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
