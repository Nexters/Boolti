package com.nexters.boolti.presentation.screen.payment

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(
    onClickHome: () -> Unit,
    onClickClose: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(onBack = onClickClose)

    Scaffold(
        topBar = { PaymentToolbar(onClickHome = onClickHome, onClickClose = onClickClose) },
        snackbarHost = {
            ToastSnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(bottom = 40.dp))
        },
    ) { innerPadding ->
        when (uiState) {
            is PaymentState.Loading -> Unit
            is PaymentState.Success -> {
                val reservation = (uiState as PaymentState.Success).reservationDetail
                when {
                    reservation.reservationState == ReservationState.RESERVED || reservation.isInviteTicket ->
                        PaymentCompleteScreen(
                            modifier = Modifier.padding(innerPadding),
                            reservation = reservation
                        )

                    else -> ProgressPayment(
                        modifier = Modifier.padding(innerPadding),
                        reservation = reservation,
                        onClickCopyAccountNumber = {
                            clipboardManager.setText(AnnotatedString(it))
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.account_number_copied_message),
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressPayment(
    modifier: Modifier = Modifier,
    reservation: ReservationDetail,
    onClickCopyAccountNumber: (accountNumber: String) -> Unit,
) {
    when (reservation.paymentType) {
        PaymentType.ACCOUNT_TRANSFER -> AccountTransferContent(
            modifier = modifier,
            reservation = reservation,
            onClickCopyAccountNumber = onClickCopyAccountNumber
        )

        PaymentType.CARD -> Unit
        PaymentType.UNDEFINED -> Unit
    }
}
