package com.nexters.boolti.presentation.screen.payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.theme.BooltiTheme

@Composable
fun PaymentScreen(
    onClickHome: () -> Unit,
    onClickClose: () -> Unit,
    navigateToReservation: (reservation: ReservationDetail) -> Unit,
    navigateToTicketDetail: (reservation: ReservationDetail) -> Unit,
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
            ToastSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        },
    ) { innerPadding ->
        when (uiState) {
            is PaymentState.Loading -> Unit
            is PaymentState.Success -> {
                val reservation = (uiState as PaymentState.Success).reservationDetail
                when {
                    reservation.totalAmountPrice == 0 ||
                            reservation.reservationState == ReservationState.RESERVED ||
                            reservation.isInviteTicket ->
                        PaymentCompleteScreen(
                            modifier = Modifier.padding(innerPadding),
                            reservation = reservation,
                            navigateToReservation = navigateToReservation,
                            navigateToTicketDetail = navigateToTicketDetail,
                        )

                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun PaymentToolbar(
    onClickHome: () -> Unit,
    onClickClose: () -> Unit,
) {
    BtAppBar(
        navigateButtons = {
            BtAppBarDefaults.AppBarIconButton(
                iconRes = R.drawable.ic_home,
                description = stringResource(R.string.description_toolbar_home),
                onClick = onClickHome,
            )
        },
        actionButtons = {
            BtAppBarDefaults.AppBarIconButton(
                iconRes = R.drawable.ic_close,
                description = stringResource(R.string.description_close_button),
                onClick = onClickClose,
            )
        }
    )
}

@Preview
@Composable
private fun PaymentToolBarPreview() {
    BooltiTheme {
        Surface {
            PaymentToolbar({}, {})
        }
    }
}
