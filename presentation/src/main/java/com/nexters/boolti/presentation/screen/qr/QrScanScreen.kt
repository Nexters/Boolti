package com.nexters.boolti.presentation.screen.qr

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
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
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.CircleBgIcon
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Success
import com.nexters.boolti.presentation.theme.Warning
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QrScanScreen(
    barcodeView: DecoratedBarcodeView,
    viewModel: QrScanViewModel = hiltViewModel(),
    onClickClose: () -> Unit,
    onClickSwitchCamera: () -> Unit,
) {
    var showEntryCodeDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarIconId by remember { mutableStateOf<Int?>(null) }

    val successMessage = stringResource(R.string.message_ticket_validated)
    val notTodayErrMessage = stringResource(R.string.error_show_not_today)
    val usedTicketErrMessage = stringResource(R.string.error_used_ticket)
    val notMatchedErrMessage = stringResource(R.string.error_ticket_not_matched)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var bottomPadding by remember { mutableStateOf(0.dp) }
    var borderColor: Color by remember { mutableStateOf(Color.Transparent) }
    var showingBorderJob: Job? = null

    val context = LocalContext.current
    val cameraSwitchable = rememberSaveable { hasBothSidesCameras(context) }

    LaunchedEffect(barcodeView) {
        barcodeView.resume()
    }

    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            val (iconId, errMessage, color) = when (event) {
                is QrScanEvent.ScanError -> {
                    when (event.errorType) {
                        QrErrorType.ShowNotToday -> Triple(
                            R.drawable.ic_warning,
                            notTodayErrMessage,
                            Warning
                        )

                        QrErrorType.UsedTicket -> Triple(
                            R.drawable.ic_error,
                            usedTicketErrMessage,
                            Error
                        )

                        QrErrorType.TicketNotFound -> Triple(
                            R.drawable.ic_error,
                            notMatchedErrMessage,
                            Error
                        )
                    }
                }

                is QrScanEvent.ScanSuccess -> Triple(
                    R.drawable.ic_error,
                    successMessage,
                    Success
                )
            }
            snackbarIconId = iconId

            launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(errMessage)
            }

            showingBorderJob?.cancel()
            showingBorderJob = launch {
                borderColor = color
                delay(4000L)
                borderColor = Color.Transparent
            }
        }
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = {
            BtAppBar(
                title = uiState.showName,
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = onClickClose,
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                actionButtons = {
                    if (cameraSwitchable) {
                        BtAppBarDefaults.AppBarIconButton(
                            onClick = onClickSwitchCamera,
                            iconRes = R.drawable.ic_camera_flip,
                        )
                    }
                }
            )
        },
        bottomBar = {
            QrScanBottombar { showEntryCodeDialog = true }
        },
        snackbarHost = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                ToastSnackbarHost(
                    modifier = Modifier
                        .statusBarsPadding()
                        .offset { // Scaffold의 inner padding 만큼 상단을 뚫고 나가는 문제가 있음. 해당 값 보정.
                            IntOffset(
                                0,
                                bottomPadding
                                    .toPx()
                                    .toInt()
                            )
                        }
                        .padding(top = 18.dp + 44.dp), // 44.dp 는 top bar 높이 값 수동 계산
                    hostState = snackbarHostState,
                    leadingIcon = {
                        snackbarIconId?.let {
                            CircleBgIcon(
                                imageVector = ImageVector.vectorResource(it),
                                bgColor = when (it) {
                                    R.drawable.ic_check -> Success
                                    R.drawable.ic_error -> Error
                                    else -> Warning
                                }
                            )
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        LaunchedEffect(innerPadding) {
            bottomPadding = innerPadding.calculateBottomPadding()
        }

        AndroidView(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .border(width = 2.dp, color = borderColor),
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(R.string.entry_code_notice),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Grey50,
            ),
        )
        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onClick, role = Role.Button)
                .padding(vertical = 20.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp),
                painter = painterResource(id = R.drawable.ic_book),
                tint = Grey30,
                contentDescription = stringResource(R.string.show_entry_code),
            )
            Text(
                text = stringResource(id = R.string.show_entry_code),
                style = MaterialTheme.typography.titleSmall,
                color = Grey30,
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
        Text(
            text = stringResource(R.string.manager_code),
            style = MaterialTheme.typography.titleLarge
        )
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

private fun hasBothSidesCameras(context: Context): Boolean {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraIds = cameraManager.cameraIdList

    var hasFrontCamera = false
    var hasBackCamera = false

    for (cameraId in cameraIds) {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)

        if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            hasFrontCamera = true
        }

        if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
            hasBackCamera = true
        }
    }

    return hasFrontCamera && hasBackCamera
}