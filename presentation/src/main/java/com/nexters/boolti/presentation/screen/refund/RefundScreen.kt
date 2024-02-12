package com.nexters.boolti.presentation.screen.refund

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.screen.reservations.ReservationDetailUiState
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2
import com.nexters.boolti.presentation.theme.point4
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefundScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RefundViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }

    Scaffold(
        topBar = {
            BtAppBar(
                title = stringResource(id = R.string.refund_button),
                onBackPressed = onBackPressed
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        val state = uiState
        if (state !is ReservationDetailUiState.Success) return@Scaffold

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = false,
        ) { index ->
            if (index == 0) {
                ReasonPage(
                    modifier = Modifier.padding(innerPadding),
                    onNextClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                )
            } else {
                RefundInfoPage(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    reservation = state.reservation,
                    onRequest = {},
                )
            }
        }
    }
}

@Composable
fun ReasonPage(
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = marginHorizontal),
            text = stringResource(id = R.string.refund_reason_label),
            style = point4,
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .height(160.dp)
                .padding(top = 20.dp)
                .clip(shape = RoundedCornerShape(4.dp)),
            value = "",
            onValueChange = {},
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Grey10),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Grey85,
                unfocusedContainerColor = Grey85,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(4.dp),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.refund_reason_hint),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Grey70),
                )
            }
        )

        Spacer(modifier = Modifier.weight(1.0f))
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 8.dp),
            onClick = onNextClick,
            enabled = true, // TODO 입력 여부
            label = stringResource(id = R.string.next)
        )
    }
}

@Composable
fun RefundInfoPage(
    reservation: ReservationDetail,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Header(
            reservation = reservation
        )
        Section(
            title = stringResource(id = R.string.refund_account_holder_info)
        ) {

        }
        Section(
            title = stringResource(id = R.string.refund_account_info)
        ) {

        }

        Spacer(modifier = Modifier.weight(1.0f))
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 8.dp),
            onClick = onRequest,
            enabled = true, // TODO 입력 여부
            label = stringResource(id = R.string.next)
        )
    }
}

@Composable
private fun Header(
    reservation: ReservationDetail,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = marginHorizontal, vertical = 20.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .width(70.dp)
                .height(98.dp)
                .border(color = Grey80, width = 1.dp, shape = RoundedCornerShape(4.dp))
                .clip(shape = RoundedCornerShape(4.dp)),
            model = reservation.showImage,
            contentDescription = stringResource(id = R.string.description_poster),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = reservation.showName,
                style = point2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(
                    id = R.string.reservation_ticket_info_format,
                    reservation.ticketName,
                    reservation.ticketCount
                ),
                style = MaterialTheme.typography.bodySmall.copy(color = Grey30),
            )
        }
    }
}

@Composable
private fun Section(
    title: String,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = true,
    expandable: Boolean = true,
    content: @Composable () -> Unit,
) {
    var expanded by remember {
        mutableStateOf(defaultExpanded)
    }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f,
        label = "rotationX"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                }
                .padding(horizontal = marginHorizontal)
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(color = Grey10),
            )
            if (expandable) {
                Icon(
                    modifier = Modifier.graphicsLayer {
                        rotationX = rotation
                    },
                    painter = painterResource(id = R.drawable.ic_expand_24),
                    contentDescription = stringResource(R.string.description_expand),
                    tint = Grey50,
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 20.dp),
            visible = expanded,
        ) {
            content()
        }
    }
}