package com.nexters.boolti.presentation.screen.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.extension.toDescriptionAndColorPair
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point1
import com.nexters.boolti.presentation.theme.subTextPadding
import java.time.format.DateTimeFormatter

@Composable
fun ReservationsScreen(
    onBackPressed: () -> Unit,
    navigateToDetail: (id: String, isGift: Boolean) -> Unit,
    viewModel: ReservationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchReservations()
    }

    Scaffold(
        topBar = {
            BtBackAppBar(
                title = stringResource(id = R.string.my_ticketing_history),
                onClickBack = onBackPressed
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when (val state = uiState) {
                ReservationsUiState.Loading -> {
                    BtCircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                ReservationsUiState.Error -> Unit // TODO 에러 화면

                is ReservationsUiState.Success -> SuccessContent(
                    reservations = state.reservations,
                    navigateToDetail = navigateToDetail,
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(
    modifier: Modifier = Modifier,
    navigateToDetail: (id: String, isGift: Boolean) -> Unit,
    reservations: List<Reservation>,
) {
    if (reservations.isNotEmpty()) {
        ReservationsContent(
            modifier = modifier,
            reservations = reservations,
            navigateToDetail = navigateToDetail,
        )
    } else {
        EmptyContent()
    }
}

@Composable
private fun ReservationsContent(
    reservations: List<Reservation>,
    navigateToDetail: (id: String, isGift: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp),
    ) {
        items(
            count = reservations.size,
            key = { reservations[it].id }) { index ->
            val isGift = reservations[index].isGift
            ReservationItem(
                reservation = reservations[index],
                navigateToDetail = {
                    val id = if (isGift) reservations[index].giftId!! else reservations[index].id
                    navigateToDetail(id, isGift)
                },
            )
        }
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.reservations_empty),
            style = MaterialTheme.typography.headlineSmall.copy(color = Grey05),
        )
        Text(
            modifier = Modifier.padding(top = subTextPadding),
            text = stringResource(id = R.string.reservations_empty_sub),
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        )
    }
}

@Composable
private fun ReservationItem(
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
        HorizontalDivider(thickness = 1.dp, color = Grey85)
        if (reservation.receiver != null) {
            Row(
                modifier = Modifier.height(34.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.ic_gift),
                    contentDescription = null,
                    tint = Grey50
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "TO. ${reservation.receiver}",
                    style = MaterialTheme.typography.titleSmall.copy(color = Grey30)
                )
            }
        }
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
                ReservationStateLabel(
                    isGift = reservation.isGift,
                    reservationState = reservation.reservationState,
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = reservation.showName,
                    overflow = TextOverflow.Ellipsis,
                    style = point1.copy(color = Grey05),
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(color = Grey30),
                    text = stringResource(
                        id = R.string.reservations_ticket_count_price_format,
                        reservation.salesTicketName,
                        reservation.ticketCount,
                        reservation.ticketPrice,
                    ),
                )
            }
        }
    }
}

@Composable
fun ReservationStateLabel(
    isGift: Boolean,
    reservationState: ReservationState,
    modifier: Modifier = Modifier,
) {
    val (stringId, color) = reservationState.toDescriptionAndColorPair(isGift)

    Text(
        modifier = modifier,
        text = stringResource(id = stringId),
        style = MaterialTheme.typography.bodySmall.copy(color = color),
    )
}
