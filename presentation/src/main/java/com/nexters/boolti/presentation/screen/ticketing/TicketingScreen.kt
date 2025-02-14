package com.nexters.boolti.presentation.screen.ticketing

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Currency
import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BusinessInformation
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.PolicyBottomSheet
import com.nexters.boolti.presentation.component.ShowItem
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.component.TopGradientBackground
import com.nexters.boolti.presentation.extension.filterToPhoneNumber
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.Success
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2
import com.nexters.boolti.presentation.util.PhoneNumberVisualTransformation
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity.Companion.RESULT_FAIL
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity.Companion.RESULT_SOLD_OUT
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity.Companion.RESULT_SUCCESS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun TicketingScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketingViewModel = hiltViewModel(),
    onBackClicked: () -> Unit = {},
    onReserved: (reservationId: String, showId: String) -> Unit,
    navigateToBusiness: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TicketingScreen(
        showId = viewModel.showId,
        salesTicketTypeId = viewModel.salesTicketTypeId,
        uiState = uiState,
        event = viewModel.event,
        onBackClicked = onBackClicked,
        onReserved = onReserved,
        navigateToBusiness = navigateToBusiness,
        onChangeReservationName = viewModel::setReservationName,
        onChangeReservationPhoneNumber = viewModel::setReservationPhoneNumber,
        onChangeDepositorName = viewModel::setDepositorName,
        onChangeDepositorPhoneNumber = viewModel::setDepositorPhoneNumber,
        onToggleIsSameContactInfo = viewModel::toggleIsSameContactInfo,
        onClickCheckInviteCode = viewModel::checkInviteCode,
        onInviteCodeChanged = viewModel::setInviteCode,
        onToggleAgreement = viewModel::toggleAgreement,
        onClickReservation = viewModel::reservation,
        modifier = modifier,
    )
}

@Composable
private fun TicketingScreen(
    showId: String,
    salesTicketTypeId: String,
    uiState: TicketingState,
    event: Flow<TicketingEvent>,
    onBackClicked: () -> Unit = {},
    onReserved: (reservationId: String, showId: String) -> Unit,
    navigateToBusiness: () -> Unit,
    onChangeReservationName: (String) -> Unit,
    onChangeReservationPhoneNumber: (String) -> Unit,
    onChangeDepositorName: (String) -> Unit,
    onChangeDepositorPhoneNumber: (String) -> Unit,
    onToggleIsSameContactInfo: () -> Unit,
    onClickCheckInviteCode: () -> Unit,
    onInviteCodeChanged: (String) -> Unit,
    onToggleAgreement: () -> Unit,
    onClickReservation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showPaymentFailureDialog by remember { mutableStateOf(false) }
    var showTicketSoldOutDialog by remember { mutableStateOf(false) }
    var policyPageUrl: String? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    var bottomButtonHeight by remember { mutableStateOf(0.dp) }

    val uiState by rememberUpdatedState(uiState)

    val paymentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_SUCCESS -> {
                    val intent = result.data ?: return@rememberLauncherForActivityResult
                    val reservationId =
                        intent.getStringExtra("reservationId")
                            ?: return@rememberLauncherForActivityResult
                    onReserved(reservationId, showId)
                }

                RESULT_SOLD_OUT -> showTicketSoldOutDialog = true
                RESULT_FAIL -> showPaymentFailureDialog = true
            }
        }

    LaunchedEffect(event) {
        event.collect {
            when (it) {
                is TicketingEvent.TicketingSuccess -> {
                    showConfirmDialog = false
                    onReserved(it.reservationId, it.showId)
                }

                is TicketingEvent.ProgressPayment -> {
                    paymentLauncher.launch(
                        TossPaymentWidgetActivity.getIntent(
                            context = context,
                            amount = uiState.totalPrice,
                            clientKey = BuildConfig.TOSS_CLIENT_KEY,
                            customerKey = "user-${it.userId}",
                            orderId = it.orderId,
                            orderName = "${showId}/${uiState.ticketName}/${uiState.ticketCount}/Android",
                            currency = Currency.KRW.name,
                            countryCode = "KR",
                            showId = showId,
                            salesTicketTypeId = salesTicketTypeId,
                            ticketCount = uiState.ticketCount,
                            reservationName = uiState.reservationName,
                            reservationPhoneNumber = uiState.reservationContact,
                            depositorName = if (uiState.isSameContactInfo) uiState.reservationName else uiState.depositorName,
                            depositorPhoneNumber = if (uiState.isSameContactInfo) uiState.reservationContact else uiState.depositorContact,
                            variantKey = null, // 멀티 결제 UI 사용 시 필요
                            redirectUrl = null, // 브랜드 페이 사용 시 필요
                        )
                    )
                    showConfirmDialog = false
                }

                is TicketingEvent.NoRemainingQuantity -> {
                    showConfirmDialog = false
                    showTicketSoldOutDialog = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.ticketing_toolbar_title),
                onClickBack = onBackClicked,
            )
        },
        snackbarHost = {
            ToastSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 100.dp)
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.padding(innerPadding)) {
            Column(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState),
            ) {
                ShowItem(
                    poster = uiState.poster,
                    showName = uiState.showName,
                    showDate = uiState.showDate,
                    showNameStyle = point2,
                    contentPadding = PaddingValues(20.dp),
                    backgroundColor = MaterialTheme.colorScheme.background,
                )
                // 예매자 정보
                TicketHolderSection(
                    name = uiState.reservationName,
                    phoneNumber = uiState.reservationContact,
                    isSameContactInfo = uiState.isSameContactInfo,
                    onNameChanged = onChangeReservationName,
                    onPhoneNumberChanged = onChangeReservationPhoneNumber,
                )

                // 입금자 정보
                if (!uiState.isInviteTicket && uiState.totalPrice > 0) {
                    DepositorSection(
                        name = uiState.depositorName,
                        phoneNumber = uiState.depositorContact,
                        isSameContactInfo = uiState.isSameContactInfo,
                        onClickSameContact = onToggleIsSameContactInfo,
                        onNameChanged = onChangeDepositorName,
                        onPhoneNumberChanged = onChangeDepositorPhoneNumber,
                    )
                }

                // 티켓 정보
                Section(title = stringResource(R.string.ticket_info_label)) {
                    TicketInfoSection(
                        ticketName = uiState.ticketName,
                        ticketCount = uiState.ticketCount,
                        totalPrice = uiState.totalPrice,
                    )
                }

                // 초청 코드
                if (uiState.isInviteTicket) {
                    InviteCodeSection(
                        uiState.inviteCode,
                        uiState.inviteCodeStatus,
                        onClickCheckInviteCode = onClickCheckInviteCode,
                        onInviteCodeChanged = onInviteCodeChanged,
                    )
                }

                if (!uiState.isInviteTicket) RefundPolicySection(uiState.refundPolicy) // 취소/환불 규정

                // 주문내용 확인 및 결제 동의
                OrderAgreementSection(
                    totalAgreed = uiState.orderAgreed,
                    agreement = uiState.orderAgreement,
                    onClickTotalAgree = onToggleAgreement,
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
                Spacer(modifier = Modifier.height(bottomButtonHeight))
            }

            TopGradientBackground(
                modifier = Modifier.align(Alignment.BottomCenter),
                onHeightChanged = { bottomButtonHeight = it },
            ) {
                MainButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp),
                    enabled = uiState.reservationButtonEnabled,
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
        if (showConfirmDialog) {
            TicketingConfirmDialog(
                isInviteTicket = uiState.isInviteTicket,
                reservationName = uiState.reservationName,
                reservationContact = uiState.reservationContact,
                depositor = if (uiState.isSameContactInfo) uiState.reservationName else uiState.depositorName,
                depositorContact = if (uiState.isSameContactInfo) uiState.reservationContact else uiState.depositorContact,
                ticketName = uiState.ticketName,
                ticketCount = uiState.ticketCount,
                totalPrice = uiState.totalPrice,
                onClick = onClickReservation,
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
                onBackClicked()
            }
        }
        policyPageUrl?.let { url ->
            PolicyBottomSheet(onDismissRequest = {
                policyPageUrl = null
            }, url = url)
        }
    }
}

@Composable
internal fun RefundPolicySection(refundPolicy: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0F else 180F,
        animationSpec = tween(),
        label = "expandIconRotation"
    )
    Section(
        title = stringResource(R.string.refund_policy_label),
        titleRowOption = {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .rotate(rotation)
                    .clickable(role = Role.Image) { expanded = !expanded },
                painter = painterResource(R.drawable.ic_expand_24),
                tint = Grey50,
                contentDescription = null,
            )
        },
        contentVisible = expanded,
    ) {
        Column(
            Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium,
                    )
                )
        ) {
            if (expanded) {
                refundPolicy.forEach {
                    Row(modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            text = stringResource(R.string.bullet),
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey50,
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey50,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InviteCodeSection(
    inviteCode: String = "",
    inviteCodeStatus: InviteCodeStatus = InviteCodeStatus.Default,
    onInviteCodeChanged: (String) -> Unit,
    onClickCheckInviteCode: () -> Unit,
) {
    Section(title = stringResource(R.string.invite_code_label)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BTTextField(
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 6.dp),
                text = inviteCode.uppercase(),
                singleLine = true,
                enabled = inviteCodeStatus !is InviteCodeStatus.Valid,
                isError = inviteCodeStatus in listOf(
                    InviteCodeStatus.Invalid,
                    InviteCodeStatus.Duplicated,
                ),
                placeholder = stringResource(R.string.ticketing_invite_code_placeholder),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    capitalization = KeyboardCapitalization.Characters,
                ),
                onValueChanged = {
                    onInviteCodeChanged(it.uppercase())
                },
            )
            Button(
                modifier = Modifier.height(48.dp),
                onClick = onClickCheckInviteCode,
                enabled = inviteCodeStatus !is InviteCodeStatus.Valid && inviteCode.isNotBlank(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Grey20,
                    disabledContainerColor = Grey80,
                    contentColor = Grey90,
                    disabledContentColor = Grey50,
                ),
            ) {
                Text(
                    text = stringResource(R.string.ticketing_invite_code_use_button),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        if (inviteCodeStatus !is InviteCodeStatus.Default) {
            val message = when (inviteCodeStatus) {
                InviteCodeStatus.Default -> ""
                InviteCodeStatus.Duplicated -> stringResource(R.string.ticketing_invite_code_duplicated)
                InviteCodeStatus.Empty -> stringResource(R.string.ticketing_invite_code_empty)
                InviteCodeStatus.Invalid -> stringResource(R.string.ticketing_invite_code_invalid)
                InviteCodeStatus.Valid -> stringResource(R.string.ticketing_invite_code_success)
            }
            val color = if (inviteCodeStatus is InviteCodeStatus.Valid) Success else Error
            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = color,
            )
        }
    }
}

@Composable
internal fun TicketInfoSection(
    ticketName: String,
    ticketCount: Int,
    totalPrice: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SectionTicketInfo(
            stringResource(R.string.ticket_type_label),
            ticketName,
            marginTop = 0.dp
        )
        SectionTicketInfo(
            label = stringResource(R.string.ticket_count_label),
            value = stringResource(R.string.ticket_count, ticketCount),
        )
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.total_payment_amount_label),
                style = MaterialTheme.typography.bodyLarge,
                color = Grey30
            )
            Text(
                text = stringResource(R.string.unit_won, totalPrice),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
    }
}

@Composable
private fun DepositorSection(
    name: String = "",
    phoneNumber: String = "",
    isSameContactInfo: Boolean,
    onClickSameContact: () -> Unit,
    onNameChanged: (name: String) -> Unit,
    onPhoneNumberChanged: (number: String) -> Unit,
) {
    Section(
        title = stringResource(R.string.depositor_info_label),
        titleRowOption = {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .clickable(role = Role.Checkbox, onClick = onClickSameContact),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isSameContactInfo) {
                    Icon(
                        painter = painterResource(R.drawable.ic_checkbox_selected),
                        tint = Grey05,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(3.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_checkbox_18),
                        tint = Grey50,
                        contentDescription = null,
                    )
                }
                Text(
                    text = stringResource(R.string.ticketing_same_contact_info),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        },
        modifier = Modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium,
            )
        ),
        contentVisible = !isSameContactInfo,
    ) {
        if (!isSameContactInfo) {
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
                imeAction = ImeAction.Default,
            ) {
                onPhoneNumberChanged(it)
            }
        }
    }
}

@Composable
private fun TicketHolderSection(
    name: String = "",
    phoneNumber: String = "",
    isSameContactInfo: Boolean,
    onNameChanged: (name: String) -> Unit,
    onPhoneNumberChanged: (number: String) -> Unit,
) {
    Section(title = stringResource(R.string.ticketing_ticket_holder_label)) {
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
            imeAction = if (isSameContactInfo) {
                ImeAction.Default
            } else {
                ImeAction.Next
            },
        ) {
            onPhoneNumberChanged(it)
        }
    }
}

@Composable
internal fun OrderAgreementSection(
    totalAgreed: Boolean,
    agreement: List<Pair<Int, Boolean>>,
    onClickTotalAgree: () -> Unit,
    onClickShow: (index: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onClickTotalAgree),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (totalAgreed) {
                Icon(
                    painter = painterResource(R.drawable.ic_checkbox_selected),
                    tint = Grey05,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(3.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_checkbox_18),
                    tint = Grey50,
                    contentDescription = null,
                )
            }
            Text(
                text = stringResource(R.string.order_agreement_label),
                color = Grey10,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.size(16.dp))
        agreement.forEachIndexed { index, (labelRes, agreed) ->
            OrderAgreementItem(
                modifier = Modifier.padding(top = 4.dp),
                index = index,
                agreed = agreed,
                label = stringResource(labelRes),
                onClickShow = onClickShow,
            )
        }
    }
}

@Composable
private fun OrderAgreementItem(
    modifier: Modifier = Modifier,
    index: Int,
    agreed: Boolean,
    label: String,
    onClickShow: (index: Int) -> Unit,
) {
    Row(modifier = modifier.fillMaxWidth(1f)) {
        Icon(
            modifier = Modifier.padding(end = 4.dp),
            painter = painterResource(R.drawable.ic_check), contentDescription = label,
            tint = if (agreed) MaterialTheme.colorScheme.primary else Grey50,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Grey50,
        )
        Spacer(modifier = Modifier.weight(1f))
        ShowButton { onClickShow(index) }
    }
}

@Composable
private fun ShowButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier.clickable(onClick = onClick),
        text = stringResource(R.string.show),
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
        ),
        color = Grey50,
    )
}

@Composable
internal fun Section(
    modifier: Modifier = Modifier,
    title: String,
    titleRowOption: (@Composable () -> Unit)? = null,
    contentVisible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
            .background((MaterialTheme.colorScheme.surface))
            .padding(start = 20.dp, end = 20.dp, bottom = if (contentVisible) 20.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SectionTitle(title)
            titleRowOption?.let { it() }
        }
        content()
    }
}

@Composable
fun InputRow(
    label: String,
    text: String,
    placeholder: String = "",
    isPhoneNumber: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onValueChanged: (String) -> Unit,
) {
    Row {
        Text(
            text = label,
            Modifier
                .width(44.dp)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.bodySmall,
        )
        BTTextField(
            text = if (isPhoneNumber) text.filterToPhoneNumber() else text,
            placeholder = placeholder,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1F),
            singleLine = true,
            keyboardOptions = if (isPhoneNumber) {
                KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = imeAction)
            } else {
                KeyboardOptions.Default.copy(imeAction = imeAction)
            },
            visualTransformation = if (isPhoneNumber) {
                PhoneNumberVisualTransformation('-')
            } else {
                VisualTransformation.None
            },
            onValueChanged = {
                onValueChanged(
                    if (isPhoneNumber) it.filterToPhoneNumber() else it,
                )
            },
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun SectionTicketInfo(label: String, value: String, marginTop: Dp = 16.dp) {
    Row(
        modifier = Modifier
            .padding(top = marginTop)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Grey30)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun TicketingDetailScreenPreview() {
    val uiState = TicketingState(
        showName = "2024 TOGETHER LUCKY CLUB",
        ticketName = "일반 티켓 B",
        ticketCount = 2,
        totalPrice = 5000,
    )
    BooltiTheme {
        Surface {
            TicketingScreen(
                showId = "",
                salesTicketTypeId = "",
                uiState = uiState,
                event = emptyFlow(),
                onReserved = { _, _ -> },
                navigateToBusiness = {},
                onChangeReservationName = {},
                onChangeReservationPhoneNumber = {},
                onChangeDepositorName = {},
                onChangeDepositorPhoneNumber = {},
                onToggleIsSameContactInfo = {},
                onToggleAgreement = {},
                onClickCheckInviteCode = {},
                onInviteCodeChanged = {},
                onClickReservation = {},
            )
        }
    }
}

@Preview
@Composable
private fun OrderAgreementItemPreview() {
    BooltiTheme {
        Surface {
            val agreed by remember { mutableStateOf(false) }
            OrderAgreementItem(
                index = 0,
                agreed = agreed,
                label = "test",
                onClickShow = {},
            )
        }
    }
}
