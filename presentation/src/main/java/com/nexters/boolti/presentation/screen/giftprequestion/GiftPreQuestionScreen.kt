package com.nexters.boolti.presentation.screen.giftprequestion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point4
import kotlinx.collections.immutable.ImmutableList

@Composable
fun GiftPreQuestionScreen(
    onBackPressed: () -> Unit,
    viewModel: GiftPreQuestionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
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
                )
            }
        }
    }
}

@Composable
fun GiftPreQuestionScreen(
    gift: Gift,
    preQuestions: ImmutableList<PreQuestion>
) {
    Column() {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = marginHorizontal),
            text = stringResource(R.string.gift_pre_question_title),
            style = point4,
            color = Grey05,
        )

        Column {
            ShowItemV2(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .padding(horizontal = marginHorizontal),
                poster = gift.showImage,
                title = gift.showName,
                description = stringResource(
                    id = R.string.reservation_ticket_info_format,
                    gift.salesTicketName,
                    gift.ticketCount,
                ),
            )
        }
    }
}