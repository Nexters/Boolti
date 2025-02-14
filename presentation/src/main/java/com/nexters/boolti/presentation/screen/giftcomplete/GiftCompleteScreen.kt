package com.nexters.boolti.presentation.screen.giftcomplete

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.SecondaryButton
import com.nexters.boolti.presentation.component.ShowItem
import com.nexters.boolti.presentation.component.TopGradientBackground
import com.nexters.boolti.presentation.component.dummyReservationDetail
import com.nexters.boolti.presentation.extension.cardCodeToCompanyName
import com.nexters.boolti.presentation.screen.payment.PaymentToolbar
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey40
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey95
import com.nexters.boolti.presentation.theme.KakaoYellow
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4
import timber.log.Timber

@Composable
fun GiftCompleteScreen(
    onClickHome: () -> Unit,
    onClickClose: () -> Unit,
    navigateToReservation: (reservation: ReservationDetail) -> Unit,
    viewModel: GiftCompleteViewModel = hiltViewModel(),
) {
    val reservationState by viewModel.reservation.collectAsStateWithLifecycle()
    val reservation = reservationState

    if (reservation == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BtCircularProgressIndicator()
        }
    } else {
        GiftCompleteScreen(
            onClickHome = onClickHome,
            onClickClose = onClickClose,
            navigateToReservation = navigateToReservation,
            reservation = reservation,
        )
    }
}

@Composable
fun GiftCompleteScreen(
    onClickHome: () -> Unit,
    onClickClose: () -> Unit,
    navigateToReservation: (reservation: ReservationDetail) -> Unit,
    reservation: ReservationDetail,
) {
    val context = LocalContext.current
    var bottomButtonHeight by remember { mutableStateOf(0.dp) }

    BackHandler(onBack = onClickClose)

    Scaffold(
        topBar = {
            PaymentToolbar(onClickHome = onClickHome, onClickClose = onClickClose)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                val month = reservation.salesEndDateTime.month.value
                val day = reservation.salesEndDateTime.dayOfMonth
                val senderText = stringResource(
                    id = R.string.gift_sender_description,
                    reservation.depositorName
                )
                val dateText = stringResource(id = R.string.gift_expiration_date, month, day)
                val buttonText = stringResource(id = R.string.gift_check)

                Text(
                    modifier = Modifier.padding(vertical = 20.dp),
                    text = stringResource(id = R.string.gift_complete_note),
                    style = point4,
                )
                HorizontalDivider(color = Grey85)
                InfoRow(
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                    label = stringResource(R.string.reservation_number),
                    value = reservation.csReservationId
                )
                InfoRow(
                    modifier = Modifier.padding(top = 8.dp),
                    label = stringResource(R.string.gift_receiver),
                    value = "${reservation.visitorName} / ${reservation.visitorPhoneNumber}"
                )
                TextButton(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = KakaoYellow),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    onClick = {
                        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
                            sendMessage(context, reservation, senderText, dateText, buttonText)
                        } else {
                            // TODO: 카카오톡 미설치 케이스 (아직은 고려 X)
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_kakaotalk),
                            contentDescription = null,
                            modifier = Modifier.size(width = 20.dp, height = 20.dp),
                            tint = Color.Black,
                        )
                        Text(
                            stringResource(id = R.string.gift_select_receiver),
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium,
                            color = Grey95,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                GiftPolicy(
                    modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                    giftPolicy = stringArrayResource(id = R.array.gift_information).toList()
                )
                HorizontalDivider(color = Grey85)
                ShowInformation(
                    reservation = reservation
                )
                Spacer(modifier = Modifier.height(bottomButtonHeight))
            }
            TopGradientBackground(
                modifier = Modifier.align(Alignment.BottomCenter),
                onHeightChanged = { bottomButtonHeight = it },
            ) {
                SecondaryButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    label = stringResource(R.string.show_reservation),
                ) {
                    navigateToReservation(reservation)
                }
            }
        }
    }
}

private fun sendMessage(
    context: Context,
    reservation: ReservationDetail,
    senderText: String,
    dateText: String,
    buttonText: String
) {
    reservation.giftUuid?.let { giftUuid ->
        sendMessage(
            context,
            giftUuid,
            senderText,
            reservation.giftInviteImage,
            dateText,
            buttonText
        )
    }
}

fun sendMessage(
    context: Context,
    giftUuid: String,
    senderText: String,
    image: String,
    dateText: String,
    buttonText: String
) {
    val domain = BuildConfig.DOMAIN
    val giftUrl = "https://${domain}/gift/$giftUuid"

    val defaultFeed = FeedTemplate(
        content = Content(
            title = senderText,
            description = dateText,
            imageUrl = image,
            link = Link(
                webUrl = giftUrl,
                mobileWebUrl = giftUrl
            )
        ),
        buttons = listOf(
            Button(
                buttonText,
                Link(
                    webUrl = giftUrl,
                    mobileWebUrl = giftUrl
                )
            ),
        )
    )

    ShareClient.instance.shareDefault(context, defaultFeed) { sharingResult, error ->
        if (error != null) {
            FirebaseCrashlytics.getInstance().recordException(error)
            Timber.e(error)
        } else if (sharingResult != null) {
            context.startActivity(sharingResult.intent)

            Timber.w("Warning Msg: ${sharingResult.warningMsg}")
            Timber.w("Argument Msg: ${sharingResult.argumentMsg}")
        }
    }
}

@Composable
private fun InfoRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    value2: String? = null,
) {
    Column(
        modifier = modifier.height(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.width(100.dp),
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey30,
            )
            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey15,
            )
        }
        value2?.let {
            Text(
                modifier = Modifier.padding(start = 112.dp),
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey15,
            )
        }
    }
}

@Composable
fun GiftPolicy(
    modifier: Modifier = Modifier,
    giftPolicy: List<String>,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        giftPolicy.forEach {
            PolicyLine(text = it)
        }
    }
}

@Composable
private fun PolicyLine(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 7.dp)
                .size(4.dp)
                .clip(shape = RoundedCornerShape(2.dp))
                .background(color = Grey40),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(color = Grey50),
        )
    }
}

@Composable
private fun ShowInformation(
    reservation: ReservationDetail,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val payment = when (reservation.paymentType) {
        PaymentType.ACCOUNT_TRANSFER -> stringResource(R.string.payment_account_transfer)
        PaymentType.CARD -> {
            val installment = reservation.cardDetail?.installmentPlanMonths?.let { months ->
                if (months == 0) {
                    stringResource(R.string.payment_pay_in_full)
                } else {
                    stringResource(R.string.payment_installment_plan_months, months)
                }
            }
            StringBuilder(reservation.cardDetail?.issuerCode?.cardCodeToCompanyName(context) ?: "")
                .apply {
                    installment?.let { append(" / $it") }
                }
                .toString()
        }

        else -> null
    }

    Column(
        modifier = modifier,
    ) {
        InfoRow(
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            label = stringResource(R.string.payment_amount_label),
            value = stringResource(
                R.string.unit_won,
                reservation.totalAmountPrice
            ),
            value2 = payment?.let { "($it)" },
        )
        InfoRow(
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            label = stringResource(R.string.reservation_ticket_type),
            value = "${reservation.ticketName} / ${
                stringResource(
                    R.string.ticket_count,
                    reservation.ticketCount
                )
            }",
        )
        ShowItem(
            modifier = Modifier.fillMaxWidth(),
            poster = reservation.showImage,
            showName = reservation.showName,
            showDate = reservation.showDate,
        )
    }
}

@Composable
@Preview(device = "spec:width=1080px,height=1340px,dpi=440")
@Preview
fun GiftCompleteScreenPreview(
) {
    BooltiTheme {
        Surface {
            GiftCompleteScreen(
                onClickClose = {},
                onClickHome = {},
                navigateToReservation = {},
                reservation = dummyReservationDetail
            )
        }
    }
}