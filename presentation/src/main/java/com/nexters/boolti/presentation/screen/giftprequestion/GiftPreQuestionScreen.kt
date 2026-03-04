package com.nexters.boolti.presentation.screen.giftprequestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtCloseableAppBar
import com.nexters.boolti.presentation.component.ShowItemV2
import com.nexters.boolti.presentation.extension.showDateString
import com.nexters.boolti.presentation.screen.refund.InfoRow
import com.nexters.boolti.presentation.screen.ticketing.PreQuestionsSection
import com.nexters.boolti.presentation.theme.Grey05
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

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = Grey95,
        topBar = {
            BtCloseableAppBar(
                onClickClose = {
                    TODO()
                    onBackPressed()
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val state = uiState

            if (state is GiftPreQuestionUiState.Success) {
                GiftPreQuestionScreen(
                    gift = state.gift,
                    preQuestions = state.preQuestions,
                    preQuestionAnswers = state.preQuestionAnswers,
                    onAnswerChanged = viewModel::putPreQuestionAnswer,
                    getAnswerError = state::getAnswerError,
                )
            }
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
        ShowItemV2(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            poster = gift.showImage,
            title = gift.showName,
            description = gift.showDate.showDateString,
        )

        InfoRow(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 8.dp),
            type = "티켓 종류",
            value = "${gift.ticketCount}매"
        )
        InfoRow(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 10.dp),
            type = "티켓 종류",
            value = "${gift.ticketCount}매"
        )
    }
}