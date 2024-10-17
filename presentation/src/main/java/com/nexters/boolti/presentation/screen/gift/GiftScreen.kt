package com.nexters.boolti.presentation.screen.gift

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Currency
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BusinessInformation
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.PolicyBottomSheet
import com.nexters.boolti.presentation.screen.ticketing.Header
import com.nexters.boolti.presentation.screen.ticketing.InputRow
import com.nexters.boolti.presentation.screen.ticketing.OrderAgreementSection
import com.nexters.boolti.presentation.screen.ticketing.PaymentFailureDialog
import com.nexters.boolti.presentation.screen.ticketing.RefundPolicySection
import com.nexters.boolti.presentation.screen.ticketing.Section
import com.nexters.boolti.presentation.screen.ticketing.TicketInfoSection
import com.nexters.boolti.presentation.theme.Grey40
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity.Companion.RESULT_FAIL
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity.Companion.RESULT_SOLD_OUT
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity.Companion.RESULT_SUCCESS

@Composable
fun GiftScreen(
    popBackStack: () -> Unit,
    navigateToBusiness: () -> Unit,
    navigateToComplete: (giftId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GiftViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showTicketSoldOutDialog by remember { mutableStateOf(false) }
    var showPaymentFailureDialog by remember { mutableStateOf(false) }
    var policyPageUrl: String? by remember { mutableStateOf(null) }

    val paymentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_SUCCESS -> {
                    val intent = result.data ?: return@rememberLauncherForActivityResult
                    val giftId = intent.getStringExtra("giftId")
                        ?: return@rememberLauncherForActivityResult

                    navigateToComplete(giftId)
                }

                RESULT_SOLD_OUT -> showTicketSoldOutDialog = true
                RESULT_FAIL -> showPaymentFailureDialog = true
            }
        }

    LaunchedEffect(viewModel.events) {
        viewModel.events
            .collect { event ->
                when (event) {
                    is GiftEvent.GiftSuccess -> {
                        navigateToComplete(event.giftId)
                    }

                    is GiftEvent.ProgressPayment -> {
                        val selectedImage = uiState.selectedImage ?: return@collect

                        paymentLauncher.launch(
                            TossPaymentWidgetActivity.getGiftIntent(
                                context = context,
                                amount = uiState.totalPrice,
                                clientKey = BuildConfig.TOSS_CLIENT_KEY,
                                customerKey = "user-${event.userId}",
                                orderId = event.orderId,
                                orderName = "${uiState.showName} ${uiState.ticketName}",
                                currency = Currency.KRW.name,
                                countryCode = "KR",
                                showId = viewModel.showId,
                                salesTicketTypeId = viewModel.salesTicketTypeId,
                                ticketCount = uiState.ticketCount,
                                senderName = uiState.senderName,
                                senderContact = uiState.senderContact,
                                receiverName = uiState.receiverName,
                                receiverContact = uiState.receiverContact,
                                message = uiState.message,
                                imageId = selectedImage.id,
                            )
                        )
                        showConfirmDialog = false
                    }

                    GiftEvent.NoRemainingQuantity -> {
                        showConfirmDialog = false
                        showTicketSoldOutDialog = true
                    }
                }
            }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                title = stringResource(id = R.string.ticketing_give_a_gift),
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(id = R.string.description_navigate_back),
                        onClick = popBackStack,
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                CardSelection(
                    message = uiState.message,
                    onMessageChanged = viewModel::updateMessage,
                    images = uiState.giftImages,
                    selectedImage = uiState.selectedImage,
                    onImageSelected = viewModel::selectImage
                )

                // 받는 분
                ContactInfo(
                    sectionName = stringResource(id = R.string.gift_receiver_info),
                    name = uiState.receiverName,
                    phoneNumber = uiState.receiverContact,
                    onNameChanged = viewModel::updateReceiverName,
                    onPhoneNumberChanged = viewModel::updateReceiverContact,
                    isReceiver = true,
                )

                // 보내는 분
                ContactInfo(
                    sectionName = stringResource(id = R.string.gift_sender_info),
                    name = uiState.senderName,
                    phoneNumber = uiState.senderContact,
                    onNameChanged = viewModel::updateSenderName,
                    onPhoneNumberChanged = viewModel::updateSenderContact,
                    isReceiver = false,
                )

                // 공연 및 티켓 정보
                Section(title = stringResource(id = R.string.gift_show_info)) {
                    Header(
                        poster = uiState.poster,
                        showName = uiState.showName,
                        showDate = uiState.showDate,
                    )
                    TicketInfoSection(
                        modifier = Modifier.padding(top = 28.dp),
                        ticketName = uiState.ticketName,
                        ticketCount = uiState.ticketCount,
                        totalPrice = uiState.totalPrice,
                    )
                }

                RefundPolicySection(uiState.refundPolicy) // 취소/환불 규정

                // 주문내용 확인 및 결제 동의
                OrderAgreementSection(
                    totalAgreed = uiState.orderAgreed,
                    agreement = uiState.orderAgreement,
                    onClickTotalAgree = viewModel::toggleAgreement,
                    onClickShow = {
                        when (it) {
                            0 -> policyPageUrl = "https://boolti.in/site-policy/privacy"
                            1 -> policyPageUrl = "https://boolti.in/site-policy/consent"
                        }
                    },
                )

                Text(
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 20.dp)
                        .padding(horizontal = marginHorizontal),
                    text = stringResource(R.string.business_responsibility),
                    style = MaterialTheme.typography.labelMedium,
                    color = Grey70,
                )

                // 사업자 정보
                BusinessInformation(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = navigateToBusiness
                )
                Spacer(modifier = Modifier.height(120.dp))
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0F),
                                    MaterialTheme.colorScheme.background,
                                )
                            ),
                            shape = RectangleShape,
                        )
                )
                MainButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                    enabled = uiState.isComplete,
                    label = stringResource(
                        R.string.ticketing_payment_button_label,
                        uiState.totalPrice
                    ),
                    onClick = {
                        showConfirmDialog = true
                    },
                )
            }
        }
    }

    if (showConfirmDialog) {
        GiftConfirmDialog(
            receiverName = uiState.receiverName,
            receiverContact = uiState.receiverContact,
            senderName = uiState.senderName,
            senderContact = uiState.senderContact,
            ticketName = uiState.ticketName,
            ticketCount = uiState.ticketCount,
            totalPrice = uiState.totalPrice,
            onClick = viewModel::pay,
            onDismiss = { showConfirmDialog = false },
        )
    }
    if (showPaymentFailureDialog) {
        PaymentFailureDialog {
            showPaymentFailureDialog = false
        }
    }
    if (showTicketSoldOutDialog) {
        PaymentFailureDialog {
            showTicketSoldOutDialog = false
            popBackStack()
        }
    }

    policyPageUrl?.let { url ->
        PolicyBottomSheet(onDismissRequest = {
            policyPageUrl = null
        }, url = url)
    }
}

@Composable
private fun ContactInfo(
    sectionName: String,
    name: String,
    phoneNumber: String,
    onNameChanged: (name: String) -> Unit,
    onPhoneNumberChanged: (number: String) -> Unit,
    isReceiver: Boolean,
) {
    Section(title = sectionName) {
        InputRow(
            stringResource(R.string.name_label),
            name,
            placeholder = stringResource(R.string.ticketing_name_placeholder),
        ) {
            onNameChanged(it)
        }
        Spacer(modifier = Modifier.size(16.dp))
        InputRow(
            stringResource(R.string.contact_label),
            phoneNumber,
            placeholder = stringResource(R.string.ticketing_contact_placeholder),
            isPhoneNumber = true,
            imeAction = ImeAction.Done,
        ) {
            onPhoneNumberChanged(it)
        }

        if (!isReceiver) return@Section

        Row(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_info_20),
                contentDescription = null,
                tint = Grey40,
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = stringResource(id = R.string.gift_receiver_note),
                style = MaterialTheme.typography.bodySmall,
                color = Grey40,
            )
        }
    }
}
