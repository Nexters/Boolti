package com.nexters.boolti.presentation.screen.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.data
import com.nexters.boolti.presentation.screen.ticket.detail.TicketDetailViewModel
import com.nexters.boolti.presentation.screen.ticketName
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter

fun NavGraphBuilder.QrFullScreen(
    popBackStack: () -> Unit,
    getSharedViewModel: @Composable (NavBackStackEntry) -> TicketDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "${MainDestination.Qr.route}/{$data}?ticketName={$ticketName}",
        arguments = MainDestination.Qr.arguments,
    ) { entry ->
        QrFullScreen(
            modifier = modifier,
            viewModel = getSharedViewModel(entry),
        ) { popBackStack() }
    }
}

@Composable
fun QrFullScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketDetailViewModel = hiltViewModel(),
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConstraintLayout(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        val (closeButton, qr) = createRefs()

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .constrainAs(closeButton) {
                    top.linkTo(parent.top, margin = 10.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                tint = Grey90,
                contentDescription = stringResource(R.string.description_close_button),
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(qr) {
                    centerTo(parent)
                }
                .background(
                    color = Grey10,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = uiState.ticketGroup.ticketName,
                style = MaterialTheme.typography.titleMedium,
                color = Grey85.copy(alpha = .85f),
            )
            Image(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .padding(14.dp),
                painter = rememberQrBitmapPainter(
                    content = uiState.ticketGroup.tickets.first().entryCode,
                    size = 260.dp,
                ),
                contentScale = ContentScale.Inside,
                contentDescription = stringResource(R.string.description_qr),
            )
            Image(
                modifier = Modifier
                    .width(84.dp)
                    .padding(bottom = 12.dp),
                painter = painterResource(R.drawable.ic_logo_boolti),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
            )
        }
    }
}
