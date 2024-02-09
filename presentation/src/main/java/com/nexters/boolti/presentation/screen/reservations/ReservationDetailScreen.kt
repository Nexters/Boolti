package com.nexters.boolti.presentation.screen.reservations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.CopyButton
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2

@Composable
fun ReservationDetailScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { ReservationDetailAppBar(onBackPressed = onBackPressed) }) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = marginHorizontal)
                    .padding(top = 12.dp),
                text = "No. 1234567890",
                style = MaterialTheme.typography.bodySmall.copy(color = Grey50),
            )
            Header()
            DepositInfo()
            PaymentInfo()
            TicketInfo()
            TicketHolderInfo()
            DepositorInfo()
            RefundPolicy()
        }
    }
}

@Composable
private fun ReservationDetailAppBar(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
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
            text = stringResource(id = R.string.reservation_detail),
            style = MaterialTheme.typography.titleMedium.copy(color = Grey10),
        )
    }
}

@Composable
private fun Header(
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
            model = "https://picsum.photos/200",
            contentDescription = stringResource(id = R.string.description_poster),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(start = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "2024 TOGETHER LUCKY CLUB",
                style = point2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "일반 티켓 B / 1매",
                style = MaterialTheme.typography.bodySmall.copy(color = Grey30),
            )
        }
    }
}

@Composable
private fun DepositInfo(
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier,
        title = stringResource(id = R.string.reservation_account_info),
    ) {
        Column {
            DepositInfoRow(
                modifier = Modifier
                    .height(32.dp)
                    .padding(bottom = 8.dp),
                key = stringResource(id = R.string.bank_name),
                value = "신한은행",
            )
            DepositInfoRow(
                modifier = Modifier
                    .height(40.dp)
                    .padding(bottom = 2.dp),
                key = stringResource(id = R.string.account_number),
                value = "1234-56-7890123",
            ) {
                CopyButton(label = "복사", onClick = { /*TODO*/ })
            }
            DepositInfoRow(
                modifier = Modifier
                    .height(40.dp)
                    .padding(bottom = 2.dp),
                key = stringResource(id = R.string.account_holder),
                value = "박불티",
            )
            DepositInfoRow(
                modifier = Modifier
                    .height(40.dp)
                    .padding(bottom = 2.dp),
                key = stringResource(id = R.string.account_transfer_due_date),
                value = "2024.01.19 23:59",
            )
        }
    }
}

@Composable
private fun DepositInfoRow(
    key: String,
    value: String,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(end = 20.dp)
                .width(80.dp),
            text = key,
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
        )
        Spacer(modifier = Modifier.weight(1.0f))
        content?.invoke()
    }
}

@Composable
private fun PaymentInfo(
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.reservation_pament_info),
    ) {
        Column {
            NormalRow(modifier = Modifier.padding(bottom = 8.dp), key = "결제 수단", value = "계좌 이체")
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = "총 결제 금액",
                value = "5,000원"
            )
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = "결제 상태",
                value = "입금 확인 중"
            )
        }
    }
}

@Composable
private fun TicketInfo(
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.reservation_ticket_info),
    ) {
        Column {
            NormalRow(modifier = Modifier.padding(bottom = 8.dp), key = "티켓 종류", value = "일반 티켓 B")
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = "티켓 개수",
                value = "1매"
            )
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = "발권 일시",
                value = "발권 전"
            )
        }
    }
}

@Composable
private fun TicketHolderInfo(
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.ticketing_ticket_holder_label),
        defaultExpanded = false,
    ) {
        Column {
            NormalRow(modifier = Modifier.padding(bottom = 8.dp), key = "이름", value = "김불티")
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = "연락처",
                value = "010-1234-5678"
            )
        }
    }
}

@Composable
private fun DepositorInfo(
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp),
        title = stringResource(id = R.string.ticketing_depositor_label),
        defaultExpanded = false,
    ) {
        Column {
            NormalRow(modifier = Modifier.padding(bottom = 8.dp), key = "이름", value = "김불티")
            NormalRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                key = "연락처",
                value = "010-1234-5678"
            )
        }
    }
}

@Composable
private fun RefundPolicy(
    modifier: Modifier = Modifier,
) {
    Section(
        modifier = modifier.padding(top = 12.dp, bottom = 40.dp),
        title = stringResource(id = R.string.ticketing_refund_policy_label),
        defaultExpanded = false,
    ) {
        // todo : 규정 변경하기
        Column {
            PolicyLine(text = "하단에 나열된 내용은 전부 임시 텍스트 입니다.\n")
            PolicyLine(text = "입장 확인이 된 티켓이 있을경우 환불이 불가합니다.\n")
            PolicyLine(text = "환불 방법 : 티켓 예매 상세내역 > 예매취소\n")
            PolicyLine(text = "환불 금액 : 당사에서는 주문에 대해 즉시 취소를\u2028하고, 취소 금액에 대한 입금은 카드사 영업일 기준 4~5일이 소요될 수 있습니다.\n")
            PolicyLine(text = "문의사항 발생시 카카오톡 채널로 문의부탁드립니다.")
        }
    }
}

@Composable
private fun PolicyLine(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(22.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 7.dp)
                .size(4.dp)
                .clip(shape = RoundedCornerShape(2.dp))
                .background(color = Grey50),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(color = Grey50),
        )
    }
}

@Composable
private fun NormalRow(
    key: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier,
            text = key,
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
        )
    }
}

@Composable
private fun Section(
    title: String,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = true,
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
            Icon(
                modifier = Modifier.graphicsLayer {
                    rotationX = rotation
                },
                painter = painterResource(id = R.drawable.ic_expand_24),
                contentDescription = stringResource(R.string.description_expand),
                tint = Grey50,
            )
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