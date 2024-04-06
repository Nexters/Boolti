package com.nexters.boolti.presentation.screen.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.nexters.boolti.domain.exception.QrErrorType
import com.nexters.boolti.presentation.QrScanEvent
import com.nexters.boolti.presentation.QrScanViewModel
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtCloseableAppBar
import com.nexters.boolti.presentation.component.CircleBgIcon
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Success
import com.nexters.boolti.presentation.theme.Warning
import kotlinx.coroutines.launch

@Composable
fun QrScanScreen(
    barcodeView: DecoratedBarcodeView,
    viewModel: QrScanViewModel = hiltViewModel(),
    onClickClose: () -> Unit,
) {
    var showEntryCodeDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarIconId by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    val successMessage = stringResource(R.string.message_ticket_validated)
    val notTodayErrMessage = stringResource(R.string.error_show_not_today)
    val usedTicketErrMessage = stringResource(R.string.error_used_ticket)
    val notMatchedErrMessage = stringResource(R.string.error_ticket_not_matched)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(barcodeView) {
        barcodeView.resume()
    }

    LaunchedEffect(viewModel.event) {
        scope.launch {
            viewModel.event.collect { event ->
                val (iconId, errMessage) = when (event) {
                    is QrScanEvent.ScanError -> {
                        when (event.errorType) {
                            QrErrorType.ShowNotToday -> Pair(R.drawable.ic_warning, notTodayErrMessage)
                            QrErrorType.UsedTicket -> Pair(R.drawable.ic_error, usedTicketErrMessage)
                            QrErrorType.TicketNotFound -> Pair(R.drawable.ic_error, notMatchedErrMessage)
                        }
                    }

                    is QrScanEvent.ScanSuccess -> Pair(R.drawable.ic_error, successMessage)
                }
                snackbarIconId = iconId
                snackbarHostState.showSnackbar(errMessage)
            }
        }
    }

    Scaffold(
        topBar = {
            BtCloseableAppBar(
                title = uiState.showName,
                onClickClose = onClickClose,
            )
        },
        bottomBar = {
            QrScanBottombar { showEntryCodeDialog = true }
        },
        snackbarHost = {
            ToastSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 100.dp),
                leadingIcon = {
                    snackbarIconId?.let {
                        CircleBgIcon(
                            painter = painterResource(it),
                            bgColor = when (it) {
                                R.drawable.ic_check -> Success
                                R.drawable.ic_error -> Error
                                else -> Warning
                            }
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            factory = { barcodeView },
        )

        if (showEntryCodeDialog) {
            EntryCodeDialog(
                managerCode = uiState.managerCode,
                onDismiss = { showEntryCodeDialog = false },
            )
        }
    }
}

@Composable
private fun QrScanBottombar(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp),
                painter = painterResource(id = R.drawable.ic_book),
                tint = Grey50,
                contentDescription = stringResource(R.string.show_entry_code),
            )
            Text(
                text = stringResource(id = R.string.show_entry_code),
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
        }
    }
}

@Composable
private fun EntryCodeDialog(
    managerCode: String,
    onDismiss: () -> Unit,
) {
    BTDialog(showCloseButton = false, onDismiss = onDismiss, onClickPositiveButton = onDismiss) {
        Text(text = stringResource(R.string.manager_code), style = MaterialTheme.typography.titleLarge)
        Text(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(vertical = 13.dp, horizontal = 12.dp),
            text = managerCode,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
