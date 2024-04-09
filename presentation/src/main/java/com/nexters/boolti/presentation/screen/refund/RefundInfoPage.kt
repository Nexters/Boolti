package com.nexters.boolti.presentation.screen.refund

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtCheckBox
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
import com.nexters.boolti.presentation.util.PhoneNumberVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefundInfoPage(
    uiState: RefundUiState,
    refundPolicy: List<String>,
    reservation: ReservationDetail,
    onRequest: () -> Unit,
    onNameChanged: (String) -> Unit,
    onContactNumberChanged: (String) -> Unit,
    onBankInfoChanged: (BankInfo) -> Unit,
    onAccountNumberChanged: (String) -> Unit,
    onRefundPolicyChecked: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showAccountError by remember { mutableStateOf(false) }

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
                            .weight(1.0f),
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
                            .weight(1.0f),
                        text = uiState.contact.filterToPhoneNumber(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        placeholder = stringResource(id = R.string.ticketing_contact_placeholder),
                        onValueChanged = onContactNumberChanged,
                        visualTransformation = PhoneNumberVisualTransformation('-'),
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
                        .padding(top = 16.dp)
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

        Section(
            modifier = Modifier.padding(top = 12.dp),
            title = stringResource(id = R.string.refund_account_info),
            expandable = false,
        ) {
            Column {
                val paymentType = when (reservation.paymentType) {
                    PaymentType.ACCOUNT_TRANSFER -> stringResource(id = R.string.payment_account_transfer)
                    PaymentType.CARD -> stringResource(id = R.string.payment_card)
                    else -> stringResource(id = R.string.reservations_unknown)
                }

                NormalRow(
                    key = stringResource(id = R.string.refund_price),
                    value = stringResource(id = R.string.unit_won, reservation.totalAmountPrice)
                )
                NormalRow(
                    modifier = Modifier.padding(top = 16.dp),
                    key = stringResource(id = R.string.refund_method),
                    value = paymentType
                )
            }
        }

        Section(
            modifier = Modifier.padding(vertical = 12.dp),
            title = stringResource(id = R.string.refund_policy_label),
            expandable = false,
        ) {
            Column {
                refundPolicy.forEach {
                    PolicyLine(modifier = Modifier.padding(bottom = 4.dp), text = it)
                }
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onRefundPolicyChecked(!uiState.refundPolicyChecked) }
                        .background(Grey85)
                        .border(width = 1.dp, color = Grey80, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BtCheckBox(
                        modifier = Modifier.size(24.dp),
                        isSelected = uiState.refundPolicyChecked,
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = R.string.refund_confirm_policy),
                        color = Grey10,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1.0f))
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .padding(top = 16.dp)
                .padding(vertical = 8.dp),
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
                .background(color = Grey50),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(color = Grey50),
        )
    }
}
