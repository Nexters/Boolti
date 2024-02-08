package com.nexters.boolti.presentation.screen.qr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey30

@Composable
fun HostedShowScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    Scaffold(
        topBar = { HostedShowToolbar(onClickBack) }
    ) { innerPadding ->
        EmptyHostedShow(modifier = modifier.padding(innerPadding))
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
