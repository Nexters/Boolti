package com.nexters.boolti.presentation.screen.giftprequestion

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtCloseableAppBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.ShowItemV2
import com.nexters.boolti.presentation.component.TopGradientBackground
import com.nexters.boolti.presentation.extension.format
import com.nexters.boolti.presentation.extension.showDateString
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.refund.InfoRow
import com.nexters.boolti.presentation.screen.ticketing.PreQuestionsSection
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.Grey95
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

@Composable
fun GiftPreQuestionScreen(
    onBackPressed: () -> Unit,
    viewModel: GiftPreQuestionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showFailureDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarController = LocalSnackbarController.current

    BackHandler { showExitDialog = true }

    val giftRegistrationMessage = stringResource(id = R.string.gift_successfully_registered)

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                GiftPreQuestionEvent.GiftRegistered -> {
                    snackbarController.showMessage(giftRegistrationMessage)
                    onBackPressed()
                }

                GiftPreQuestionEvent.GiftRegistrationFailed -> {
                    showFailureDialog = true
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = Grey95,
        topBar = {
            BtCloseableAppBar(
                onClickClose = { showExitDialog = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val state = uiState

            if (state is GiftPreQuestionUiState.Success) {
                GiftPreQuestionScreen(
                    gift = state.gift,
                    preQuestions = state.preQuestions,
                    preQuestionAnswers = state.preQuestionAnswers,
                    onAnswerChanged = viewModel::putPreQuestionAnswer,
                    getAnswerError = state::getAnswerError,
                )
                GiftPreQuestionCta(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    salesEndTime = state.gift.salesEndTime.format(stringResource(R.string.gift_pre_question_deadline_date_format)),
                    receiveGift = viewModel::receiveGift,
                    enabled = state.isPreQuestionsValid
                )
            }
        }
    }

    if (showExitDialog) {
        BTDialog(
            onDismiss = { showExitDialog = false },
            negativeButtonLabel = stringResource(R.string.cancel),
            onClickNegativeButton = { showExitDialog = false },
            positiveButtonLabel = stringResource(R.string.gift_pre_question_exit_label),
            onClickPositiveButton = onBackPressed,
        ) {
            Text(
                text = stringResource(R.string.gift_pre_question_exit_dialog_title),
                color = Grey15,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }

    if (showFailureDialog) {
        BTDialog(
            onDismiss = { showFailureDialog = false },
            onClickPositiveButton = { showFailureDialog = false },
        ) {
            Text(
                text = stringResource(id = R.string.gift_registration_failed),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Grey15,
                    textAlign = TextAlign.Center
                ),
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(id = R.string.gift_registration_failed_dialog),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Grey50,
                    textAlign = TextAlign.Center
                ),
            )
        }
    }
}

@Composable
fun GiftPreQuestionScreen(
    gift: Gift,
    preQuestions: ImmutableList<PreQuestion>,
    preQuestionAnswers: ImmutableMap<Long, String>,
    onAnswerChanged: (Long, String) -> Unit,
    getAnswerError: (Long) -> Boolean,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp),
    ) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = marginHorizontal),
            text = stringResource(R.string.gift_pre_question_title),
            style = point4,
            color = Grey05,
        )

        TicketSection(
            modifier = Modifier.padding(top = 28.dp),
            gift = gift,
        )

        PreQuestionsSection(
            modifier = Modifier.padding(top = 12.dp),
            preQuestions = preQuestions,
            answers = preQuestionAnswers,
            onAnswerChanged = onAnswerChanged,
            getAnswerError = getAnswerError,
        )
    }
}

@Composable
private fun GiftPreQuestionCta(
    salesEndTime: String,
    receiveGift: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    TopGradientBackground(
        modifier = modifier,
        bgColor = Grey95,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = buildAnnotatedString {
                    val dateEnd = salesEndTime.length
                    append(stringResource(R.string.gift_pre_question_deadline, salesEndTime))
                    addStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        start = 0,
                        end = dateEnd,
                    )
                },
                color = Grey20,
                style = MaterialTheme.typography.bodySmall,
            )

            MainButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(vertical = 8.dp),
                label = stringResource(R.string.gift_pre_question_register_cta),
                enabled = enabled,
                onClick = receiveGift,
            )
        }
    }
}

@Composable
private fun TicketSection(
    gift: Gift,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Grey90)
            .padding(vertical = 20.dp)
            .padding(horizontal = marginHorizontal),
    ) {
        Text(text = stringResource(R.string.gift_show_info), color = Grey10, style = MaterialTheme.typography.titleLarge)
        ShowItemV2(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            poster = gift.showImage,
            title = gift.showName,
            description = gift.showDate.showDateString,
        )

        InfoRow(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 8.dp),
            type = stringResource(R.string.ticket_type_label),
            value = gift.salesTicketName
        )
        InfoRow(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 10.dp),
            type = stringResource(R.string.ticket_count_label),
            value = stringResource(R.string.reservation_ticket_count_format, gift.ticketCount)
        )
    }
}