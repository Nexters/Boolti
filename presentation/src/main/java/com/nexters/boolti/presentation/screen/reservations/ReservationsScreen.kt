package com.nexters.boolti.presentation.screen.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.Reservation
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Success
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point1
import java.time.format.DateTimeFormatter

@Composable
fun ReservationsScreen(
    onBackPressed: () -> Unit,
    navigateToDetail: (reservationId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReservationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { ReservationsAppBar(onBackPressed = onBackPressed) }
    ) { innerPadding ->
        if (uiState.reservations.isEmpty()) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.reservations_empty),
                    style = MaterialTheme.typography.titleLarge.copy(color = Grey05),
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResource(id = R.string.reservations_empty_sub),
                    style = MaterialTheme.typography.titleLarge.copy(color = Grey30),
                )
            }

            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(top = 20.dp)
        ) {
            items(count = uiState.reservations.size, key = { uiState.reservations[it].id }) { index ->
                ReservationItem(
                    reservation = uiState.reservations[index],
                    navigateToDetail = { navigateToDetail(uiState.reservations[index].id) },
                )
            }
        }
    }
}

@Composable
private fun ReservationsAppBar(
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
            text = stringResource(id = R.string.my_ticketing_history),
            style = MaterialTheme.typography.titleMedium.copy(color = Grey10),
        )
    }
}

@Composable
fun ReservationItem(
    reservation: Reservation,
    navigateToDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable(onClick = navigateToDetail)
            .padding(horizontal = marginHorizontal)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val format = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
            Text(
                text = reservation.reservationDateTime.format(format),
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
            Spacer(modifier = Modifier.weight(1.0f))
            Text(
                text = stringResource(id = R.string.reservations_detail_view),
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
            Icon(
                modifier = Modifier.padding(start = 4.dp),
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = Grey50
            )
        }
        Divider(thickness = 1.dp, color = Grey85)
        Row(
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(width = 60.dp, height = 84.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .border(shape = RoundedCornerShape(4.dp), color = Grey60, width = 0.5.dp),
                model = reservation.showImage,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.description_poster),
            )
            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                ReservationStateLabel(reservationState = reservation.reservationState)
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = reservation.showName,
                    overflow = TextOverflow.Ellipsis,
                    style = point1.copy(color = Grey05),
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(color = Grey30),
                    text = "${reservation.salesTicketName} / ${reservation.ticketCount}매 / ${reservation.ticketPrice}원",
                )
            }
        }
    }
}

@Composable
fun ReservationStateLabel(
    modifier: Modifier = Modifier,
    reservationState: ReservationState,
) {
    val (stringId, color) = when (reservationState) {
        ReservationState.DEPOSITING -> Pair(R.string.reservations_depositing, Grey30)
        ReservationState.REFUNDING -> Pair(R.string.reservations_refunding, Success)
        ReservationState.CANCELED -> Pair(R.string.reservations_canceled, Error)
        ReservationState.RESERVED -> Pair(R.string.reservations_reserved, Grey30)
        ReservationState.REFUNDED -> Pair(R.string.reservations_refunded, Error)
        ReservationState.UNDEFINED -> Pair(R.string.reservations_unknown, Error)
    }

    Text(
        modifier = modifier,
        text = stringResource(id = stringId),
        style = MaterialTheme.typography.bodySmall.copy(color = color),
    )
}