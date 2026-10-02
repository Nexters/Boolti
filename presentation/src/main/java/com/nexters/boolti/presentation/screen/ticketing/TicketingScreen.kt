package com.nexters.boolti.presentation.screen.ticketing

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.view
import com.nexters.boolti.common.tracker.field.Payment
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.domain.model.Currency
import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BusinessInformation
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.PolicyBottomSheet
import com.nexters.boolti.presentation.component.ShowItemV2
import com.nexters.boolti.presentation.component.TopGradientBackground
import com.nexters.boolti.presentation.screen.LocalSnackbarController
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
import com.nexters.boolti.presentation.util.DigitOnlyInputTransformation
import com.nexters.boolti.presentation.util.ObserveAsEvents
import com.nexters.boolti.presentation.util.PhoneNumberOutputTransformation
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity.Companion.RESULT_FAIL
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity.Companion.RESULT_SOLD_OUT
import com.nexters.boolti.tosspayments.TossPaymentWidgetActivity.Companion.RESULT_SUCCESS

@Composable
fun TicketingScreen(
    onBackClicked: () -> Unit,
    onReserved: (reservationId: String, showId: String) -> Unit,
    navigateToBusiness: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TicketingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resources = LocalResources.current
    val snackbarController = LocalSnackbarController.current

    val loadedState = uiState as? TicketingUiState.Success
    LaunchedEffect(loadedState != null) {
        loadedState?.let { state ->
            AppTracker.view(
                screen = Screen.Payment,
                properties = mapOf(
                    "booking_type" to "Direct",
                    "show_id" to state.showId,
                    "ticket_type" to if (state.isInviteTicket) "Invite" else "Normal",
                    "ticket_quantity" to state.ticketCount,
                    "total_amount" to state.totalPrice,
                ),
            )
        }
    }

    val paymentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_SUCCESS -> result.data?.getStringExtra("reservationId")?.let { reservationId ->
                    viewModel.onAction(TicketingAction.PaymentSucceeded(reservationId))
                }

                RESULT_SOLD_OUT -> viewModel.onAction(TicketingAction.PaymentSoldOut)
                RESULT_FAIL -> viewModel.onAction(TicketingAction.PaymentFailed)
            }
        }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is TicketingEvent.NavigateToPaymentComplete -> onReserved(event.reservationId, event.showId)
            is TicketingEvent.ShowErrorMessage -> snackbarController.showMessage(resources.getString(event.messageRes))
            is TicketingEvent.LaunchPayment -> {
                val state = event.ticketing
                paymentLauncher.launch(
                    TossPaymentWidgetActivity.getIntent(
                        context = context,
                        amount = state.totalPrice,
                        clientKey = BuildConfig.TOSS_CLIENT_KEY,
                        customerKey = "user-${event.userId}",
                        orderId = event.orderId,
                        orderName = "${state.showId}/${state.ticketName}/${state.ticketCount}/Android",
                        currency = Currency.KRW.name,
                        countryCode = "KR",
                        showId = state.showId,
                        salesTicketTypeId = state.salesTicketTypeId,
                        ticketCount = state.ticketCount,
                        reservationName = state.reservationName,
                        reservationPhoneNumber = state.reservationContact,
                        depositorName = state.depositor,
                        depositorPhoneNumber = state.depositorPhoneNumber,
                        variantKey = null, // 멀티 결제 UI 사용 시 필요
                        redirectUrl = null, // 브랜드 페이 사용 시 필요
                    )
                )
            }
        }
    }

    TicketingScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBackClicked = onBackClicked,
        navigateToBusiness = navigateToBusiness,
        modifier = modifier,
    )
}

@Composable
private fun TicketingScreen(
    uiState: TicketingUiState,
    onAction: (TicketingAction) -> Unit,
    onBackClicked: () -> Unit,
    navigateToBusiness: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.ticketing_toolbar_title),
                onClickBack = onBackClicked,
            )
        },
    ) { innerPadding ->
        val contentModifier = modifier.padding(innerPadding)
        when (uiState) {
            TicketingUiState.Loading -> Box(
                modifier = contentModifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                BtCircularProgressIndicator()
            }

            TicketingUiState.LoadFailed -> LoadErrorContent(
                modifier = contentModifier.fillMaxSize(),
                onClickRetry = { onAction(TicketingAction.RetryLoad) },
            )

            is TicketingUiState.Success -> TicketingContent(
                uiState = uiState,
                onAction = onAction,
                onBackClicked = onBackClicked,
                navigateToBusiness = navigateToBusiness,
                modifier = contentModifier,
            )
        }
    }
}

@Composable
private fun TicketingContent(
    uiState: TicketingUiState.Success,
    onAction: (TicketingAction) -> Unit,
    onBackClicked: () -> Unit,
    navigateToBusiness: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    var bottomButtonHeight by remember { mutableStateOf(0.dp) }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState),
        ) {
            // 티켓 정보
            Section(
                title = stringResource(R.string.ticket_info_label),
            ) {
                ShowItemV2(
                    modifier = Modifier.fillMaxWidth(),
                    poster = uiState.poster,
                    showName = uiState.showName,
                    showDate = uiState.showDate,
                )
                TicketInfoSection(
                    modifier = Modifier.padding(top = 20.dp),
                    ticketName = uiState.ticketName,
                    ticketCount = uiState.ticketCount,
                    totalPrice = uiState.totalPrice,
                )
            }

            // 예매자 정보
            TicketHolderSection(
                name = uiState.reservationName,
                phoneNumber = uiState.reservationContact,
                isSameContactInfo = uiState.isSameContactInfo,
                onNameChanged = { onAction(TicketingAction.ChangeReservationName(it)) },
                onPhoneNumberChanged = { onAction(TicketingAction.ChangeReservationContact(it)) },
            )

            // 입금자 정보
            if (!uiState.isInviteTicket && uiState.totalPrice > 0) {
                DepositorSection(
                    name = uiState.depositorName,
                    phoneNumber = uiState.depositorContact,
                    isSameContactInfo = uiState.isSameContactInfo,
                    onClickSameContact = { onAction(TicketingAction.ToggleSameContactInfo) },
                    onNameChanged = { onAction(TicketingAction.ChangeDepositorName(it)) },
                    onPhoneNumberChanged = { onAction(TicketingAction.ChangeDepositorContact(it)) },
                )
            }

            // 초청 코드
            if (uiState.isInviteTicket) {
                InviteCodeSection(
                    uiState.inviteCode,
                    uiState.inviteCodeStatus,
                    onClickCheckInviteCode = { onAction(TicketingAction.CheckInviteCode) },
                    onInviteCodeChanged = { onAction(TicketingAction.ChangeInviteCode(it)) },
                )
            }

            // 사전 질문
            PreQuestionsSection(
                preQuestions = uiState.preQuestions,
                answers = uiState.preQuestionAnswers,
                onAnswerChanged = { id, answer -> onAction(TicketingAction.ChangePreQuestionAnswer(id, answer)) },
                getAnswerError = uiState::getAnswerError,
            )

            if (!uiState.isInviteTicket) RefundPolicySection(uiState.refundPolicy) // 취소/환불 규정

            // 주문내용 확인 및 결제 동의
            OrderAgreementSection(
                totalAgreed = uiState.orderAgreed,
                agreement = uiState.orderAgreement,
                onClickTotalAgree = { onAction(TicketingAction.ToggleAgreement) },
                onClickShow = {
                    when (it) {
                        0 -> onAction(TicketingAction.ShowPolicy("https://boolti.in/site-policy/privacy"))
                        1 -> onAction(TicketingAction.ShowPolicy("https://boolti.in/site-policy/consent"))
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
                onClick = { onAction(TicketingAction.ClickPayment) },
            )
        }
    }
    when (uiState.dialog) {
        TicketingDialog.Confirm -> TicketingConfirmDialog(
            isInviteTicket = uiState.isInviteTicket,
            reservationName = uiState.reservationName,
            reservationContact = uiState.reservationContact,
            depositor = uiState.depositor,
            depositorContact = uiState.depositorPhoneNumber,
            ticketName = uiState.ticketName,
            ticketCount = uiState.ticketCount,
            totalPrice = uiState.totalPrice,
            onClick = { onAction(TicketingAction.ConfirmReservation) },
            onDismiss = { onAction(TicketingAction.DismissDialog) },
        )

        TicketingDialog.PaymentFailure -> PaymentFailureDialog { onAction(TicketingAction.DismissDialog) }
        TicketingDialog.SoldOut -> PaymentFailureDialog(onClickButton = onBackClicked)
        null -> Unit
    }
    uiState.policyPageUrl?.let { url ->
        PolicyBottomSheet(
            onDismissRequest = { onAction(TicketingAction.DismissPolicy) },
            url = url,
        )
    }
}

@Composable
private fun LoadErrorContent(
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.ticketing_load_error),
            style = MaterialTheme.typography.headlineSmall,
        )
        MainButton(
            modifier = Modifier.padding(top = 20.dp),
            label = stringResource(R.string.retry),
            onClick = onClickRetry,
        )
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
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column {
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
            text = text,
            placeholder = placeholder,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1F),
            keyboardOptions = if (isPhoneNumber) {
                KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = imeAction)
            } else {
                KeyboardOptions.Default.copy(imeAction = imeAction)
            },
            inputTransformation = if (isPhoneNumber) {
                InputTransformation.maxLength(11).then(
                    DigitOnlyInputTransformation()
                )
            } else {
                null
            },
            outputTransformation = if (isPhoneNumber) {
                PhoneNumberOutputTransformation("-")
            } else {
                null
            },
            onValueChanged = onValueChanged,
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
    val uiState = TicketingUiState.Success(
        showName = "2024 TOGETHER LUCKY CLUB",
        ticketName = "일반 티켓 B",
        ticketCount = 2,
        totalPrice = 5000,
    )
    BooltiTheme {
        Surface {
            TicketingScreen(
                uiState = uiState,
                onAction = {},
                onBackClicked = {},
                navigateToBusiness = {},
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
