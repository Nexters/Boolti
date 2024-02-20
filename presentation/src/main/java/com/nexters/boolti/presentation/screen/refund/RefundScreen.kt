package com.nexters.boolti.presentation.screen.refund

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.extension.filterToPhoneNumber
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2
import com.nexters.boolti.presentation.theme.point4
import com.nexters.boolti.presentation.util.PhoneNumberVisualTransformation
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefundScreen(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RefundViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val events = viewModel.events
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState { 2 }
    var openDialog by remember { mutableStateOf(false) }

    val refundMessage = stringResource(id = R.string.refund_completed)
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                RefundEvent.SuccessfullyRefunded -> {
                    // TODO 스낵바로 변경
                    Toast.makeText(context, refundMessage, Toast.LENGTH_LONG).show()
                    onBackPressed()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            BtAppBar(
                title = stringResource(id = R.string.refund_button), onBackPressed = onBackPressed
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        val reservation = uiState.reservation ?: return@Scaffold

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
                    onReasonChanged = viewModel::updateReason,
                    reason = uiState.reason
                )
            } else {
                RefundInfoPage(
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    reservation = reservation,
                    onNameChanged = viewModel::updateName,
                    onContactNumberChanged = viewModel::updateContact,
                    onBankInfoChanged = viewModel::updateBankInfo,
                    onAccountNumberChanged = viewModel::updateAccountNumber,
                    onRequest = { openDialog = true },
                )
            }
        }
    }

    if (openDialog) {
        BTDialog(
            positiveButtonLabel = stringResource(id = R.string.refund_button),
            onDismiss = { openDialog = false },
            onClickPositiveButton = viewModel::refund,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.refund_dialog_title),
                    style = MaterialTheme.typography.titleLarge.copy(color = Grey15),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Grey80)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    InfoRow(
                        type = stringResource(id = R.string.account_holder),
                        value = uiState.name
                    )
                    InfoRow(
                        modifier = Modifier.padding(top = 8.dp),
                        type = stringResource(id = R.string.contact_label),
                        value = StringBuilder(uiState.contact).apply {
                            if (uiState.contact.length > 7) {
                                insert(7, '-')
                                insert(3, '-')
                            }
                        }.toString()
                    )
                    InfoRow(
                        modifier = Modifier.padding(top = 8.dp),
                        type = stringResource(id = R.string.bank_name),
                        value = uiState.bankInfo?.bankName ?: ""
                    )
                    InfoRow(
                        modifier = Modifier.padding(top = 8.dp),
                        type = stringResource(id = R.string.account_number),
                        value = uiState.accountNumber
                    )
                }
            }
        }
    }
}

@Composable
fun ReasonPage(
    reason: String,
    onReasonChanged: (String) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            keyboardController?.hide()
        }
    ) {
        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = marginHorizontal),
            text = stringResource(id = R.string.refund_reason_label),
            style = point4,
        )
        BTTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .height(160.dp)
                .padding(top = 20.dp),
            text = reason,
            onValueChanged = onReasonChanged,
            placeholder = stringResource(id = R.string.refund_reason_hint),
        )

        Spacer(modifier = Modifier.weight(1.0f))
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 8.dp),
            onClick = onNextClick,
            enabled = reason.isNotBlank(),
            label = stringResource(id = R.string.next)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefundInfoPage(
    uiState: RefundUiState,
    reservation: ReservationDetail,
    onRequest: () -> Unit,
    onNameChanged: (String) -> Unit,
    onContactNumberChanged: (String) -> Unit,
    onBankInfoChanged: (BankInfo) -> Unit,
    onAccountNumberChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showNameError by remember { mutableStateOf(false) }
    var showAccountError by remember { mutableStateOf(false) }
    var showContactError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Header(
            reservation = reservation
        )
        Section(
            title = stringResource(id = R.string.refund_account_holder_info)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.width(56.dp),
                        text = stringResource(id = R.string.name_label),
                        style = MaterialTheme.typography.bodySmall.copy(color = Grey30),
                    )
                    BTTextField(
                        modifier = Modifier
                            .weight(1.0f)
                            .onFocusChanged { focusState ->
                                showNameError = uiState.name.isNotEmpty() &&
                                        !uiState.isValidName &&
                                        !focusState.isFocused
                            },
                        isError = showNameError,
                        text = uiState.name,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        placeholder = stringResource(id = R.string.refund_account_name_hint),
                        onValueChanged = onNameChanged
                    )
                }
                if (showNameError) {
                    Text(
                        modifier = Modifier.padding(start = 56.dp, top = 12.dp),
                        text = stringResource(id = R.string.validation_name),
                        style = MaterialTheme.typography.bodySmall.copy(color = Error),
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.width(56.dp),
                        text = stringResource(id = R.string.contact_label),
                        style = MaterialTheme.typography.bodySmall.copy(color = Grey30),
                    )
                    BTTextField(
                        modifier = Modifier
                            .weight(1.0f)
                            .onFocusChanged { focusState ->
                                showContactError =
                                    uiState.contact.isNotEmpty() &&
                                            !uiState.isValidContact &&
                                            !focusState.isFocused
                            },
                        text = uiState.contact.filterToPhoneNumber(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        isError = showContactError,
                        placeholder = stringResource(id = R.string.ticketing_contact_placeholder),
                        onValueChanged = onContactNumberChanged,
                        visualTransformation = PhoneNumberVisualTransformation('-'),
                    )
                }
                if (showContactError) {
                    Text(
                        modifier = Modifier.padding(start = 56.dp, top = 12.dp),
                        text = stringResource(id = R.string.validation_contact),
                        style = MaterialTheme.typography.bodySmall.copy(color = Error),
                    )
                }
            }
        }
        Section(
            modifier = Modifier.padding(top = 12.dp),
            title = stringResource(id = R.string.refund_account_info),
            expandable = false,
        ) {
            Column {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = { isSheetOpen = true },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceTint,
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    val bankSelection = stringResource(id = R.string.refund_bank_selection)
                    Text(
                        text = if (uiState.bankInfo == null) bankSelection else uiState.bankInfo.bankName,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Grey15),
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = stringResource(id = R.string.refund_bank_selection),
                        tint = Grey50,
                    )
                }
                BTTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .onFocusChanged { focusState ->
                            showAccountError = uiState.accountNumber.isNotEmpty() &&
                                    !uiState.isValidAccountNumber &&
                                    !focusState.isFocused
                        },
                    text = uiState.accountNumber,
                    isError = showAccountError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    placeholder = stringResource(id = R.string.refund_account_number_hint),
                    onValueChanged = onAccountNumberChanged,
                )
                if (showAccountError) {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = stringResource(id = R.string.validation_account),
                        style = MaterialTheme.typography.bodySmall.copy(color = Error),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1.0f))
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 8.dp),
            onClick = onRequest,
            enabled = uiState.isAbleToRequest,
            label = stringResource(id = R.string.refund_button)
        )
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.heightIn(max = 646.dp),
            sheetState = sheetState,
            onDismissRequest = {
                isSheetOpen = false
            },
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 20.dp)
                        .size(45.dp, 4.dp)
                        .background(Grey70)
                        .clip(RoundedCornerShape(100.dp)),
                )
            },
            containerColor = Grey85,
        ) {
            BankSelection(
                selectedBank = uiState.bankInfo,
                onClick = onBankInfoChanged,
                onDismiss = { isSheetOpen = false })
        }
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
            modifier = Modifier.padding(start = 16.dp),
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
        targetValue = if (expanded) 0f else 180f, label = "rotationX"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface),
    ) {
        val touchAreaModifier = if (expandable) {
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        } else {
            Modifier.fillMaxWidth()
        }
        Row(
            modifier = touchAreaModifier
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

@Composable
fun BankSelection(
    onDismiss: () -> Unit,
    selectedBank: BankInfo?,
    onClick: (bankInfo: BankInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(bottom = 48.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = marginHorizontal)
                .padding(bottom = 48.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 12.dp),
                text = stringResource(id = R.string.refund_bank_selection),
                style = MaterialTheme.typography.titleLarge,
            )
            LazyVerticalGrid(
                contentPadding = PaddingValues(vertical = 12.dp),
                columns = GridCells.Adaptive(minSize = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BankInfo.entries.forEach { bankInfo ->
                    item {
                        BackItem(
                            bankInfo = bankInfo,
                            onClick = onClick,
                            selected = if (selectedBank == null) null else selectedBank == bankInfo,
                        )
                    }
                }
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Grey85,
                            )
                        )
                    )
            )
            MainButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
                label = stringResource(id = R.string.refund_select_bank),
                onClick = onDismiss,
                enabled = selectedBank != null,
            )
        }
    }
}

@Composable
fun BackItem(
    onClick: (bankInfo: BankInfo) -> Unit,
    bankInfo: BankInfo,
    modifier: Modifier = Modifier,
    selected: Boolean? = null,
) {
    Box(
        modifier = modifier
            .height(74.dp)
            .border(
                shape = RoundedCornerShape(4.dp),
                color = if (selected == true) Grey10 else Color.Transparent,
                width = 1.dp,
            )
            .clip(RoundedCornerShape(4.dp))
            .background(Grey80)
            .clickable {
                onClick(bankInfo)
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.alpha(alpha = if (selected == false) 0.4f else 1.0f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = modifier.size(32.dp),
                painter = painterResource(bankInfo.icon),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = bankInfo.bankName,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun InfoRow(
    type: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = type,
            style = MaterialTheme.typography.bodySmall.copy(color = Grey30),
        )
        Text(
            modifier = Modifier.weight(1.0f),
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(color = Grey15),
            textAlign = TextAlign.End,
        )
    }
}