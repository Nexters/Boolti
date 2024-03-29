package com.nexters.boolti.presentation.screen.refund

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefundScreen(
    onBackPressed: () -> Unit,
    viewModel: RefundViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refundPolicy by viewModel.refundPolicy.collectAsStateWithLifecycle()
    val events = viewModel.events
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }
    var openDialog by remember { mutableStateOf(false) }
    val snackbarController = LocalSnackbarController.current

    val refundMessage = stringResource(id = R.string.refund_completed)
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                RefundEvent.SuccessfullyRefunded -> {
                    snackbarController.showMessage(refundMessage)
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
        modifier = Modifier,
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
                    refundPolicy = refundPolicy,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    reservation = reservation,
                    onNameChanged = viewModel::updateName,
                    onContactNumberChanged = viewModel::updateContact,
                    onBankInfoChanged = viewModel::updateBankInfo,
                    onAccountNumberChanged = viewModel::updateAccountNumber,
                    onRequest = { openDialog = true },
                    onRefundPolicyChecked = viewModel::toggleRefundPolicyCheck,
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
                        modifier = Modifier.padding(top = 12.dp),
                        type = stringResource(id = R.string.contact_label),
                        value = StringBuilder(uiState.contact).apply {
                            if (uiState.contact.length > 7) {
                                insert(7, '-')
                                insert(3, '-')
                            }
                        }.toString()
                    )
                    InfoRow(
                        modifier = Modifier.padding(top = 12.dp),
                        type = stringResource(id = R.string.bank_name),
                        value = uiState.bankInfo?.bankName ?: ""
                    )
                    InfoRow(
                        modifier = Modifier.padding(top = 12.dp),
                        type = stringResource(id = R.string.account_number),
                        value = uiState.accountNumber
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 12.dp),
                        thickness = 1.dp,
                        color = Grey70,
                    )
                    InfoRow(
                        modifier = Modifier.padding(top = 12.dp),
                        type = stringResource(id = R.string.refund_price),
                        value = stringResource(
                            id = R.string.unit_won,
                            uiState.reservation!!.totalAmountPrice,
                        )
                    )
                }
            }
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
            modifier = Modifier.padding(start = 12.dp),
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(color = Grey15),
        )
    }
}
